import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import {
  Certificado,
  Generador,
  HojaRuta,
  HojaRutaConTickets,
  Residuo,
  TicketControl,
  TipoResiduo,
  Transportista
} from '../../core/models/operations.models';
import { CertificadoService } from '../../core/services/certificado.service';
import { GeneradorService } from '../../core/services/generador.service';
import { TipoResiduoService } from '../../core/services/tipo-residuo.service';
import { TransportistaService } from '../../core/services/transportista.service';

@Component({
  selector: 'app-transportista-certificados',
  templateUrl: './transportista-certificados.component.html',
  styleUrl: './transportista-certificados.component.scss'
})
export class TransportistaCertificadosComponent implements OnInit {
  transportista: Transportista | null = null;
  certificados: Certificado[] = [];
  certificadoSeleccionado: Certificado | null = null;
  hojas: HojaRutaConTickets[] = [];
  hojasPendientes: HojaRuta[] = [];
  hojaPendienteSeleccionada: HojaRuta | null = null;
  generadores = new Map<number, Generador>();
  tiposResiduo = new Map<number, TipoResiduo>();
  ticketSeleccionado: TicketControl | null = null;
  isLoading = true;
  isLoadingDetalle = false;
  isPrintingTicket = false;
  isPrintingCertificado = false;
  message = '';
  ticketPages: Record<number, number> = {};

  readonly pageSize = 12;

  constructor(
    private readonly certificadoService: CertificadoService,
    private readonly generadorService: GeneradorService,
    private readonly tipoResiduoService: TipoResiduoService,
    private readonly transportistaService: TransportistaService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    forkJoin({
      transportista: this.transportistaService.getCurrent(),
      generadores: this.generadorService.listAll(),
      tiposResiduo: this.tipoResiduoService.listAll(),
    }).subscribe({
      next: ({ transportista, generadores, tiposResiduo }) => {
        this.transportista = transportista;
        this.generadores = new Map(generadores.map((generador) => [generador.id, generador]));
        this.tiposResiduo = new Map(tiposResiduo.map((tipo) => [tipo.id, tipo]));
        this.certificadoService.procesarHojasVencidas().subscribe(() => {
          this.loadCertificados(transportista.idTransportista);
          this.loadHojasPendientes(transportista.idTransportista);
        });
      },
      error: () => {
        this.isLoading = false;
        this.message = 'No se pudo cargar el transportista autenticado y sus generadores.';
      },
    });
  }

  get totalHojas(): number {
    return this.hojas.length;
  }

  get totalTickets(): number {
    return this.hojas.reduce((total, hoja) => total + hoja.tickets.length, 0);
  }

  get pesoTotal(): number {
    return this.hojas.reduce((total, hoja) => total + hoja.pesoTotal, 0);
  }

  get resumenSeleccion(): string {
    if (this.certificadoSeleccionado) {
      return this.periodo(this.certificadoSeleccionado);
    }

    if (this.hojaPendienteSeleccionada) {
      return this.etiquetaHojaRuta(this.hojaPendienteSeleccionada);
    }

    return 'Sin seleccion';
  }

  get textoMetricasHojas(): string {
    return this.hojaPendienteSeleccionada
      ? 'Hoja pendiente seleccionada'
      : 'Vinculadas al certificado';
  }

  get textoMetricasTickets(): string {
    return this.hojaPendienteSeleccionada
      ? 'Tickets de la hoja seleccionada'
      : 'Manifiestos del periodo';
  }

  get transportistaNombre(): string {
    if (!this.transportista) {
      return 'Transportista';
    }

    return `${this.transportista.nombre} ${this.transportista.apellido}`;
  }

  seleccionarCertificado(certificado: Certificado): void {
    if (this.certificadoSeleccionado?.id === certificado.id) {
      return;
    }

    this.certificadoSeleccionado = certificado;
    this.hojaPendienteSeleccionada = null;
    this.ticketSeleccionado = null;
    this.ticketPages = {};
    this.loadDetalle(certificado);
  }

  seleccionarHojaPendiente(hoja: HojaRuta): void {
    if (!this.certificadoSeleccionado && this.hojaPendienteSeleccionada?.id === hoja.id) {
      return;
    }

    this.certificadoSeleccionado = null;
    this.hojaPendienteSeleccionada = hoja;
    this.ticketSeleccionado = null;
    this.hojas = [];
    this.ticketPages = {};
    this.isLoadingDetalle = true;
    this.message = '';

    this.certificadoService.getTicketsDeHoja(hoja.id).subscribe({
      next: (tickets) => {
        this.hojas = [{
          ...hoja,
          tickets,
          cantidadTickets: tickets.length,
          pesoTotal: tickets.reduce((total, ticket) => total + Number(ticket.pesoTotal ?? 0), 0),
        }];
        this.isLoadingDetalle = false;
      },
      error: () => {
        this.hojas = [];
        this.isLoadingDetalle = false;
        this.message = 'No se pudieron cargar los tickets de la hoja de ruta pendiente.';
      },
    });
  }

  abrirTicket(ticket: TicketControl): void {
    this.ticketSeleccionado = ticket;

    this.scrollToTicketDetail();
  }

  private scrollToTicketDetail(): void {
    if (typeof window === 'undefined' || typeof document === 'undefined') {
      return;
    }

    window.setTimeout(() => {
      const target = document.getElementById('ticket-detail-header');

      if (!target) {
        return;
      }

      const scrollOffset = 22;
      const scroller = target.closest('.content') as HTMLElement | null;

      if (scroller) {
        const scrollerRect = scroller.getBoundingClientRect();
        const targetRect = target.getBoundingClientRect();
        const top = scroller.scrollTop + targetRect.top - scrollerRect.top - scrollOffset;

        scroller.scrollTo({
          top: Math.max(top, 0),
          behavior: 'smooth',
        });
        return;
      }

      window.scrollTo({
        top: Math.max(target.getBoundingClientRect().top + window.scrollY - scrollOffset, 0),
        behavior: 'smooth',
      });
    });
  }

  imprimirCertificado(): void {
    const certificado = this.certificadoSeleccionado;

    if (!certificado || this.isPrintingCertificado) {
      return;
    }

    this.isPrintingCertificado = true;
    this.message = '';

    this.certificadoService.downloadCertificado(certificado.id).subscribe({
      next: (pdf) => {
        this.isPrintingCertificado = false;
        this.openPdf(pdf, `certificado-${certificado.numeroCertificado ?? certificado.id}.pdf`);
      },
      error: () => {
        this.isPrintingCertificado = false;
        this.message = 'No se pudo generar el certificado PDF.';
      },
    });
  }

  imprimirManifiesto(ticket: TicketControl): void {
    if (this.isPrintingTicket) {
      return;
    }

    this.isPrintingTicket = true;
    this.message = '';

    this.certificadoService.downloadManifiestoTicket(ticket.idTicket).subscribe({
      next: (pdf) => {
        this.isPrintingTicket = false;
        this.openPdf(pdf, `manifiesto-ticket-${ticket.numeroTicket ?? ticket.idTicket}.pdf`);
      },
      error: () => {
        this.isPrintingTicket = false;
        this.message = 'No se pudo generar el manifiesto PDF.';
      },
    });
  }

  nuevoTicketEnHoja(hoja: HojaRuta): void {
    void this.router.navigate(['/transportista/tickets/nuevo'], {
      queryParams: { hojaRutaId: hoja.id },
    });
  }

  editarTicket(ticket: TicketControl): void {
    void this.router.navigate(['/transportista/tickets', ticket.idTicket], {
      queryParams: { returnTo: 'certificados' },
    });
  }

  ticketsPaginados(hoja: HojaRutaConTickets): TicketControl[] {
    const page = this.ticketPage(hoja);
    const start = (page - 1) * this.pageSize;

    return hoja.tickets.slice(start, start + this.pageSize);
  }

  ticketPage(hoja: HojaRutaConTickets): number {
    return Math.min(this.ticketPages[hoja.id] ?? 1, this.ticketTotalPages(hoja));
  }

  ticketTotalPages(hoja: HojaRutaConTickets): number {
    return Math.max(Math.ceil(hoja.tickets.length / this.pageSize), 1);
  }

  ticketRangeStart(hoja: HojaRutaConTickets): number {
    if (!hoja.tickets.length) {
      return 0;
    }

    return (this.ticketPage(hoja) - 1) * this.pageSize + 1;
  }

  ticketRangeEnd(hoja: HojaRutaConTickets): number {
    return Math.min(this.ticketPage(hoja) * this.pageSize, hoja.tickets.length);
  }

  setTicketPage(hoja: HojaRutaConTickets, page: number): void {
    this.ticketPages = {
      ...this.ticketPages,
      [hoja.id]: Math.min(Math.max(page, 1), this.ticketTotalPages(hoja)),
    };
  }

  periodo(certificado?: Certificado | null): string {
    if (!certificado) {
      return '-';
    }

    return `${this.nombreMes(certificado.mes)} ${certificado.anio}`;
  }

  peso(value: number): string {
    return `${Number(value ?? 0).toLocaleString('es-AR')} kg`;
  }

  residuoNombre(residuo: Residuo): string {
    const tipo = this.tiposResiduo.get(residuo.tipoResiduoId);

    if (!tipo) {
      return `Tipo #${residuo.tipoResiduoId}`;
    }

    return `${tipo.codigo} - ${tipo.nombre}`;
  }

  estadoTicket(ticket: TicketControl): string {
    return ticket.estado ? 'Procesado' : 'Pendiente';
  }

  etiquetaTicket(ticket: TicketControl): string {
    return `#${ticket.numeroTicket ?? ticket.idTicket}`;
  }

  etiquetaTicketCompleta(ticket: TicketControl): string {
    return `Ticket #${ticket.numeroTicket ?? ticket.idTicket}`;
  }

  etiquetaHojaRuta(hoja: HojaRuta): string {
    return `HR-${hoja.numeroHojaRuta ?? hoja.id}`;
  }

  etiquetaCertificado(certificado: Certificado): string {
    return `CERT-${certificado.numeroCertificado ?? certificado.id}`;
  }

  etiquetaHojaDeTicket(ticket: TicketControl): string {
    const hoja = this.hojas.find((item) => item.id === ticket.hojaRutaId)
      ?? this.hojasPendientes.find((item) => item.id === ticket.hojaRutaId);

    return hoja ? this.etiquetaHojaRuta(hoja) : `HR-${ticket.hojaRutaId}`;
  }

  nombreGenerador(generadorId: number): string {
    const generador = this.generadores.get(generadorId);

    if (!generador) {
      return `Generador #${generadorId}`;
    }

    const nombreAutonomo = [generador.nombre, generador.apellido].filter(Boolean).join(' ');

    const nombre = generador.razonSocial ?? generador.nombreFantasia ?? nombreAutonomo;

    return nombre || `Generador #${generadorId}`;
  }

  private loadCertificados(transportistaId: number): void {
    this.certificadoService.getByTransportista(transportistaId).subscribe({
      next: (certificados) => {
        this.certificados = certificados;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.message = 'No se pudieron cargar los certificados.';
      },
    });
  }

  private loadHojasPendientes(transportistaId: number): void {
    this.certificadoService.getHojasPendientesCertificado(transportistaId).subscribe({
      next: (hojas) => {
        this.hojasPendientes = hojas;
      },
      error: () => {
        this.hojasPendientes = [];
      },
    });
  }

  private loadDetalle(certificado: Certificado): void {
    this.isLoadingDetalle = true;
    this.message = '';
    this.hojas = [];
    this.ticketSeleccionado = null;
    this.ticketPages = {};

    this.certificadoService.getHojasConTickets(certificado).subscribe({
      next: (hojas) => {
        this.hojas = hojas;
        this.ticketSeleccionado = null;
        this.isLoadingDetalle = false;
      },
      error: () => {
        this.hojas = [];
        this.ticketSeleccionado = null;
        this.isLoadingDetalle = false;
        this.message = 'No se pudieron cargar las hojas de ruta del certificado.';
      },
    });
  }

  private nombreMes(mes: string): string {
    return mes.charAt(0) + mes.slice(1).toLowerCase();
  }

  private openPdf(pdf: Blob, fileName: string): void {
    if (typeof window === 'undefined' || typeof document === 'undefined') {
      return;
    }

    const url = window.URL.createObjectURL(pdf);
    const link = document.createElement('a');
    link.href = url;
    link.target = '_blank';
    link.download = fileName;
    link.click();

    window.setTimeout(() => window.URL.revokeObjectURL(url), 1000);
  }
}
