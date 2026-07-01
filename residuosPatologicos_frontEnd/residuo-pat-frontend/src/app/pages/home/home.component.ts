import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Certificado,
  Generador,
  HojaRuta,
  TicketControl,
  TipoResiduo,
  TransportistaDashboardData,
  TransportistaProfileStatus,
  Vehiculo
} from '../../core/models/operations.models';
import { TransportistaDashboardService } from '../../core/services/transportista-dashboard.service';
import { TransportistaService } from '../../core/services/transportista.service';
import { CertificadoService } from '../../core/services/certificado.service';

interface TaskItem {
  label: string;
  value: number;
  total: number;
  state: 'done' | 'pending' | 'warning';
}

interface ManifiestoRegistro {
  generador: Generador;
  ticket: TicketControl;
  fechaEstimada: string;
  hojaRuta: string;
  certificado: string;
  peso: number;
  estado: 'Pendiente de revision' | 'Procesado';
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  data: TransportistaDashboardData | null = null;
  profileStatus: TransportistaProfileStatus | null = null;
  isLoading = true;
  isPrintingInforme = false;
  isPrintingManifiestoId: number | null = null;
  manifiestoParaEditar: ManifiestoRegistro | null = null;
  homeMessage = '';
  manifiestosFiltro: 'todos' | 'pendientes' = 'todos';
  manifiestosPage = 1;

  readonly pageSize = 12;

  constructor(
    private readonly dashboardService: TransportistaDashboardService,
    private readonly transportistaService: TransportistaService,
    private readonly certificadoService: CertificadoService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const procesado = params.get('manifiestoProcesado');
      const generado = params.get('manifiestoGenerado');

      if (procesado) {
        this.homeMessage = `Manifiesto Ticket #${procesado} procesado correctamente.`;
        return;
      }

      if (generado) {
        this.homeMessage = `Manifiesto Ticket #${generado} generado como pendiente.`;
      }
    });

    this.transportistaService.getProfileStatus().subscribe({
      next: (status) => {
        this.profileStatus = status;
      },
      error: () => {
        this.profileStatus = null;
      },
    });

    this.dashboardService.load().subscribe((data) => {
      this.data = data;
      this.isLoading = false;
    });
  }

  get ticketsDeHojaActual(): TicketControl[] {
    const tickets = this.data?.ticketsActuales ?? [];
    const hoja = this.data?.hojaActual;

    if (!hoja) {
      return [];
    }

    return tickets.filter((ticket) => {
      return ticket.hojaRutaId === hoja.id
        && ticket.fechaEmision >= hoja.fechaInicio
        && ticket.fechaEmision <= hoja.fechaFin;
    });
  }

  get totalTickets(): number {
    return this.ticketsDeHojaActual.length;
  }

  get ticketsCerrados(): number {
    return this.ticketsDeHojaActual.filter((ticket) => ticket.estado).length;
  }

  get ticketsPendientes(): number {
    return Math.max(this.totalTickets - this.ticketsCerrados, 0);
  }

  get mostrarAvisoProcesamiento(): boolean {
    return !this.isLoading && this.totalTickets > 0 && this.ticketsPendientes > 0;
  }

  get generadoresCount(): number {
    return this.data?.generadoresActivos.filter((generador) => generador.estado).length ?? 0;
  }

  get certificadosGenerados(): number {
    return this.data?.certificadosTotal ?? this.data?.certificados?.length ?? 0;
  }

  get certificadosAnioActual(): number {
    const anioActual = new Date().getFullYear();

    return this.data?.certificados?.filter((certificado) => certificado.anio === anioActual).length ?? 0;
  }

  get pesoTotal(): number {
    return this.ticketsDeHojaActual.reduce((total, ticket) => total + Number(ticket.pesoTotal ?? 0), 0);
  }

  get cumplimientoPorcentaje(): number {
    if (!this.totalTickets) {
      return 0;
    }

    return Math.round((this.ticketsCerrados / this.totalTickets) * 100);
  }

  get transportistaPrincipal(): string {
    const transportista = this.data?.transportistas[0];

    if (!transportista) {
      return 'Transportista sin asignar';
    }

    return `${transportista.nombre} ${transportista.apellido}`;
  }

  get vehiculosActivos(): Vehiculo[] {
    return this.data?.transportistas[0]?.vehiculos?.filter((vehiculo) => vehiculo.activo) ?? [];
  }

  get ticketsRecientes(): TicketControl[] {
    return this.ticketsDeHojaActual.slice(0, 5);
  }

  get hojasSinCertificar(): number {
    return this.data?.hojasPendientesCertificado?.length ?? 0;
  }

  get manifiestosMensualesGenerados(): number {
    return this.data?.ticketsPeriodoMensual?.length ?? 0;
  }

  get hojaPendienteMasAntigua(): string {
    const hojas = [...(this.data?.hojasPendientesCertificado ?? [])]
      .sort((a, b) => a.fechaInicio.localeCompare(b.fechaInicio));
    const hoja = hojas[0];

    if (!hoja) {
      return 'Sin hojas pendientes';
    }

    return `${this.etiquetaHojaRuta(hoja)} - ${this.formatearFechaCorta(hoja.fechaInicio)} al ${this.formatearFechaCorta(hoja.fechaFin)}`;
  }

  get estadoPendientesDocumentales(): string {
    if (!this.hojasSinCertificar) {
      return 'Documentacion al dia';
    }

    return `${this.hojasSinCertificar} hojas requieren revision para certificado`;
  }

  get periodoHojaActual(): string {
    const hoja = this.data?.hojaActual;

    if (!hoja) {
      return 'Sin periodo activo';
    }

    return `${this.formatearFechaCorta(hoja.fechaInicio)} al ${this.formatearFechaCorta(hoja.fechaFin)}`;
  }

  get periodoDetalleHojaActual(): string {
    const hoja = this.data?.hojaActual;

    if (!hoja) {
      return 'Crea o espera la asignacion de una hoja de ruta para comenzar a cargar manifiestos.';
    }

    return `${this.etiquetaHojaRuta(hoja)} - ${this.diasDelPeriodo(hoja.fechaInicio, hoja.fechaFin)} dias de operacion`;
  }

  get certificadoHojaActual(): string {
    const fechaBase = this.parseDate(this.data?.hojaActual?.fechaInicio);

    if (!fechaBase) {
      return 'Sin periodo';
    }

    const mes = this.mesCertificado(fechaBase.getMonth());
    const anio = fechaBase.getFullYear();
    const certificado = this.data?.certificados?.find((item) => item.anio === anio && item.mes === mes);

    return certificado ? this.etiquetaCertificado(certificado) : `Pendiente ${mes}/${anio}`;
  }

  get tareasOperativas(): TaskItem[] {
    return [
      {
        label: 'Manifiestos procesados',
        value: this.ticketsCerrados,
        total: this.totalTickets,
        state: this.ticketsPendientes === 0 && this.totalTickets > 0 ? 'done' : 'pending',
      },
      {
        label: 'Certificados del anio',
        value: this.certificadosAnioActual,
        total: 12,
        state: this.certificadosAnioActual >= 12 ? 'done' : (this.certificadosAnioActual ? 'pending' : 'warning'),
      },
      {
        label: 'Vehiculos disponibles',
        value: this.vehiculosActivos.length,
        total: Math.max(this.data?.transportistas[0]?.vehiculos?.length ?? 0, 1),
        state: this.vehiculosActivos.length ? 'done' : 'warning',
      },
    ];
  }

  get manifiestos(): ManifiestoRegistro[] {
    const generadores = this.data?.generadoresActivos ?? [];

    return this.ticketsDeHojaActual.map((ticket) => {
      const generador = generadores.find((item) => item.id === ticket.generadorId)
        ?? this.generadorPlaceholder(ticket.generadorId);
      const peso = Number(ticket.pesoTotal ?? 0);

      return {
        generador,
        ticket,
        fechaEstimada: this.formatearFecha(ticket.fechaEmision),
        hojaRuta: this.etiquetaHojaDeTicket(ticket),
        certificado: this.certificadoMensual(),
        peso,
        estado: this.estadoManifiesto(ticket),
      };
    });
  }

  get manifiestosVisibles(): ManifiestoRegistro[] {
    if (this.manifiestosFiltro === 'pendientes') {
      return this.manifiestos.filter((registro) => registro.estado !== 'Procesado');
    }

    return this.manifiestos;
  }

  get manifiestosTotalPages(): number {
    return Math.max(Math.ceil(this.manifiestosVisibles.length / this.pageSize), 1);
  }

  get manifiestosPaginaActual(): number {
    return Math.min(this.manifiestosPage, this.manifiestosTotalPages);
  }

  get manifiestosPaginados(): ManifiestoRegistro[] {
    const start = (this.manifiestosPaginaActual - 1) * this.pageSize;

    return this.manifiestosVisibles.slice(start, start + this.pageSize);
  }

  get manifiestosDesde(): number {
    if (!this.manifiestosVisibles.length) {
      return 0;
    }

    return (this.manifiestosPaginaActual - 1) * this.pageSize + 1;
  }

  get manifiestosHasta(): number {
    return Math.min(this.manifiestosPaginaActual * this.pageSize, this.manifiestosVisibles.length);
  }

  get manifiestosCount(): number {
    return this.manifiestos.length;
  }

  get manifiestosVisiblesCount(): number {
    return this.manifiestosVisibles.length;
  }

  get manifiestosPendientesCount(): number {
    return this.manifiestos.filter((registro) => registro.estado !== 'Procesado').length;
  }

  residuosPorTipo(): Array<{ nombre: string; peso: number; count: number; porcentaje: number }> {
    const tipos = new Map<number, TipoResiduo>();
    const resumen = new Map<number, { peso: number; count: number }>();

    for (const tipo of this.data?.tiposResiduo ?? []) {
      tipos.set(tipo.id, tipo);
    }

    for (const ticket of this.ticketsDeHojaActual) {
      for (const residuo of ticket.residuos ?? []) {
        const current = resumen.get(residuo.tipoResiduoId) ?? { peso: 0, count: 0 };
        current.peso += Number(residuo.peso ?? 0);
        current.count += 1;
        resumen.set(residuo.tipoResiduoId, current);
      }
    }

    return Array.from(resumen.entries()).map(([tipoId, value]) => ({
      nombre: tipos.get(tipoId)?.nombre ?? `Tipo ${tipoId}`,
      peso: value.peso,
      count: value.count,
      porcentaje: this.pesoTotal ? Math.round((value.peso / this.pesoTotal) * 100) : 0,
    })).slice(0, 4);
  }

  taskProgress(task: TaskItem): number {
    if (!task.total) {
      return 0;
    }

    return Math.min(Math.round((task.value / task.total) * 100), 100);
  }

  nombreGenerador(generador: Generador): string {
    return generador.razonSocial
      ?? generador.nombreFantasia
      ?? [generador.nombre, generador.apellido].filter(Boolean).join(' ')
      ?? 'Generador sin nombre';
  }

  etiquetaTicket(ticket: TicketControl): string {
    return `Ticket #${ticket.numeroTicket ?? ticket.idTicket}`;
  }

  etiquetaHojaRuta(hoja: HojaRuta): string {
    return `HR-${hoja.numeroHojaRuta ?? hoja.id}`;
  }

  etiquetaCertificado(certificado: Certificado): string {
    return `CERT-${certificado.numeroCertificado ?? certificado.id}`;
  }

  pesoTicket(ticket?: TicketControl): string {
    if (!ticket) {
      return '-';
    }

    return `${Number(ticket.pesoTotal ?? 0).toLocaleString('es-AR')} kg`;
  }

  nuevoTicket(): void {
    void this.router.navigate(['/transportista/tickets/nuevo']);
  }

  filtrarManifiestos(filtro: 'todos' | 'pendientes'): void {
    this.manifiestosFiltro = filtro;
    this.manifiestosPage = 1;
  }

  setManifiestosPage(page: number): void {
    this.manifiestosPage = Math.min(Math.max(page, 1), this.manifiestosTotalPages);
  }

  imprimirInforme(): void {
    const hojaActual = this.data?.hojaActual;
    const hojaRutaId = hojaActual?.id;

    if (!hojaRutaId || this.isPrintingInforme) {
      return;
    }

    this.isPrintingInforme = true;

    this.dashboardService.downloadInformeHojaRuta(hojaRutaId).subscribe({
      next: (pdf) => {
        this.isPrintingInforme = false;
        this.openPdf(pdf, `informe-hoja-ruta-${hojaActual?.numeroHojaRuta ?? hojaRutaId}.pdf`);
      },
      error: () => {
        this.isPrintingInforme = false;
        this.homeMessage = 'No se pudo generar el informe PDF de la hoja de ruta.';
      },
    });
  }

  abrirManifiesto(registro: ManifiestoRegistro): void {
    if (registro.ticket.estado) {
      this.solicitarEdicionManifiesto(registro);
      return;
    }

    void this.router.navigate(['/transportista/tickets', registro.ticket.idTicket]);
  }

  solicitarEdicionManifiesto(registro: ManifiestoRegistro): void {
    this.manifiestoParaEditar = registro;
  }

  cancelarEdicionManifiesto(): void {
    this.manifiestoParaEditar = null;
  }

  confirmarEdicionManifiesto(): void {
    const registro = this.manifiestoParaEditar;

    if (!registro) {
      return;
    }

    this.manifiestoParaEditar = null;
    void this.router.navigate(['/transportista/tickets', registro.ticket.idTicket]);
  }

  imprimirManifiesto(registro: ManifiestoRegistro): void {
    const ticketId = registro.ticket.idTicket;
    const numeroTicket = registro.ticket.numeroTicket ?? ticketId;

    if (this.isPrintingManifiestoId) {
      return;
    }

    this.isPrintingManifiestoId = ticketId;

    this.certificadoService.downloadManifiestoTicket(ticketId).subscribe({
      next: (pdf) => {
        this.isPrintingManifiestoId = null;
        this.openPdf(pdf, `manifiesto-ticket-${numeroTicket}.pdf`);
      },
      error: () => {
        this.isPrintingManifiestoId = null;
        this.homeMessage = 'No se pudo generar el PDF del manifiesto.';
      },
    });
  }

  private estadoManifiesto(ticket: TicketControl): ManifiestoRegistro['estado'] {
    if (ticket.estado) {
      return 'Procesado';
    }

    return 'Pendiente de revision';
  }

  private certificadoMensual(): string {
    const fechaBase = this.parseDate(this.data?.hojaActual?.fechaInicio) ?? new Date();
    const mes = this.mesCertificado(fechaBase.getMonth());
    const anio = fechaBase.getFullYear();
    const certificado = this.data?.certificados?.find((item) => item.anio === anio && item.mes === mes);

    return certificado ? this.etiquetaCertificado(certificado) : `Pendiente ${mes}/${anio}`;
  }

  private etiquetaHojaDeTicket(ticket: TicketControl): string {
    const hojaActual = this.data?.hojaActual;

    if (hojaActual && hojaActual.id === ticket.hojaRutaId) {
      return this.etiquetaHojaRuta(hojaActual);
    }

    const hojaPendiente = this.data?.hojasPendientesCertificado?.find((hoja) => hoja.id === ticket.hojaRutaId);

    if (hojaPendiente) {
      return this.etiquetaHojaRuta(hojaPendiente);
    }

    return `HR-${ticket.hojaRutaId}`;
  }

  private mesCertificado(monthIndex: number): string {
    return [
      'ENERO',
      'FEBRERO',
      'MARZO',
      'ABRIL',
      'MAYO',
      'JUNIO',
      'JULIO',
      'AGOSTO',
      'SEPTIEMBRE',
      'OCTUBRE',
      'NOVIEMBRE',
      'DICIEMBRE',
    ][monthIndex] ?? 'MES';
  }

  private parseDate(value?: string): Date | null {
    if (!value) {
      return null;
    }

    const [year, month, day] = value.split('-').map(Number);

    if (!year || !month || !day) {
      return null;
    }

    return new Date(year, month - 1, day);
  }

  private formatearFecha(value: string): string {
    const date = this.parseDate(value);

    if (!date) {
      return 'A definir';
    }

    return date.toLocaleDateString('es-AR', {
      weekday: 'short',
      day: '2-digit',
      month: '2-digit',
    });
  }

  private formatearFechaCorta(value: string): string {
    const date = this.parseDate(value);

    if (!date) {
      return 'A definir';
    }

    return date.toLocaleDateString('es-AR', {
      day: '2-digit',
      month: 'short',
    });
  }

  private diasDelPeriodo(fechaInicio: string, fechaFin: string): number {
    const inicio = this.parseDate(fechaInicio);
    const fin = this.parseDate(fechaFin);

    if (!inicio || !fin) {
      return 0;
    }

    const msPorDia = 24 * 60 * 60 * 1000;

    return Math.max(Math.round((fin.getTime() - inicio.getTime()) / msPorDia) + 1, 1);
  }

  private generadorPlaceholder(generadorId: number): Generador {
    return {
      id: generadorId,
      tipo: 'EMPRESA',
      estado: true,
      razonSocial: `Generador #${generadorId}`,
    };
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
