import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Generador,
  HojaRuta,
  ResiduoRequest,
  TicketControl,
  TipoResiduo,
  Transportista,
  TransportistaDashboardData,
} from '../../core/models/operations.models';
import { TicketService } from '../../core/services/ticket.service';
import { HojaRutaService } from '../../core/services/hoja-ruta.service';
import { TipoResiduoService } from '../../core/services/tipo-residuo.service';
import { TransportistaContextService } from '../../core/services/transportista-context.service';
import { TransportistaDashboardService } from '../../core/services/transportista-dashboard.service';

@Component({
  selector: 'app-transportista-ticket-form',
  templateUrl: './transportista-ticket-form.component.html',
  styleUrl: './transportista-ticket-form.component.scss'
})
export class TransportistaTicketFormComponent implements OnInit {
  form!: FormGroup;
  ticketActual: TicketControl | null = null;
  transportista: Transportista | null = null;
  hojaActual: HojaRuta | null = null;
  generadores: Generador[] = [];
  tiposActivos: TipoResiduo[] = [];
  isLoading = true;
  isSaving = false;
  message = '';

  private ticketId: number | null = null;
  private requestedHojaRutaId: number | null = null;
  private returnTo: 'home' | 'certificados' = 'home';

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly contextService: TransportistaContextService,
    private readonly dashboardService: TransportistaDashboardService,
    private readonly hojaRutaService: HojaRutaService,
    private readonly tipoResiduoService: TipoResiduoService,
    private readonly ticketService: TicketService
  ) {}

  ngOnInit(): void {
    this.ticketId = Number(this.route.snapshot.paramMap.get('id')) || null;
    this.requestedHojaRutaId = Number(this.route.snapshot.queryParamMap.get('hojaRutaId')) || null;
    this.returnTo = this.route.snapshot.queryParamMap.get('returnTo') === 'certificados'
      ? 'certificados'
      : 'home';
    this.form = this.fb.group({
      generadorId: [null, Validators.required],
      fechaEmision: [null, Validators.required],
      residuos: this.fb.array([]),
    });

    this.load();
  }

  get isEditMode(): boolean {
    return Boolean(this.ticketId);
  }

  get pageTitle(): string {
    if (!this.isEditMode) {
      return 'Nuevo manifiesto';
    }

    return this.ticketActual?.estado ? 'Editar manifiesto' : 'Completar manifiesto';
  }

  get submitLabel(): string {
    if (this.isSaving) {
      return 'Guardando...';
    }

    if (!this.isEditMode) {
      return 'Generar manifiesto pendiente';
    }

    return this.ticketActual?.estado ? 'Actualizar manifiesto' : 'Guardar kilaje y procesar';
  }

  get hojaContextLabel(): string {
    if (this.isEditMode) {
      return 'Hoja de ruta del manifiesto';
    }

    return this.requestedHojaRutaId ? 'Hoja de ruta seleccionada' : 'Hoja de ruta actual';
  }

  get residuos(): FormArray {
    return this.form.get('residuos') as FormArray;
  }

  get totalPeso(): number {
    return this.residuos.controls.reduce((total, control) => {
      return total + Number(control.get('peso')?.value ?? 0);
    }, 0);
  }

  get minFecha(): string | null {
    return this.hojaActual?.fechaInicio ?? null;
  }

  get maxFecha(): string | null {
    return this.hojaActual?.fechaFin ?? null;
  }

  load(): void {
    this.isLoading = true;
    this.message = '';

    this.contextService.getCurrentTransportista().subscribe((transportista) => {
      this.transportista = transportista;

      if (!transportista) {
        this.isLoading = false;
        this.message = 'No se encontro un transportista asociado a la sesion.';
        return;
      }

      this.loadCatalogs();
    });
  }

  loadCatalogs(): void {
    this.dashboardService.load().subscribe({
      next: (data) => this.applyDashboardData(data),
      error: () => {
        this.generadores = [];
        this.hojaActual = null;
        this.message = 'No se pudo cargar la hoja actual y los generadores.';
        this.loadTiposActivos();
      },
    });
  }

  loadTiposActivos(): void {
    this.tipoResiduoService.listActivos().subscribe({
      next: (tipos) => {
        this.tiposActivos = tipos;

        if (this.isEditMode && this.ticketId) {
          this.loadTicket(this.ticketId);
          return;
        }

        this.isLoading = false;
      },
      error: () => {
        this.tiposActivos = [];
        this.isLoading = false;
        this.message = 'No se pudieron cargar los tipos de residuo activos.';
      },
    });
  }

  submit(): void {
    this.message = '';

    if (!this.transportista || !this.hojaActual) {
      this.message = 'Necesitas un transportista y una hoja de ruta actual para operar manifiestos.';
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.message = this.isEditMode
        ? 'Selecciona generador, fecha, tipo de residuo activo y peso mayor a cero.'
        : 'Selecciona generador y fecha presunta de visita.';
      return;
    }

    if (!this.fechaDentroDeHoja(this.form.value.fechaEmision)) {
      this.message = 'La fecha presunta debe estar dentro de la hoja de ruta seleccionada.';
      return;
    }

    if (this.isEditMode) {
      this.completarManifiesto();
      return;
    }

    this.generarManifiestoPendiente();
  }

  addResiduo(): void {
    this.residuos.push(this.fb.group({
      tipoResiduoId: [this.tiposActivos[0]?.id ?? null, Validators.required],
      peso: [null, [Validators.required, Validators.min(0.001)]],
    }));
  }

  removeResiduo(index: number): void {
    this.residuos.removeAt(index);

    if (this.isEditMode && !this.residuos.length) {
      this.addResiduo();
    }
  }

  preventInvalidPesoInput(event: KeyboardEvent): void {
    if (['-', '+', 'e', 'E'].includes(event.key)) {
      event.preventDefault();
    }
  }

  showPesoWarning(index: number): boolean {
    const control = this.residuos.at(index).get('peso');

    return Boolean(control && (control.touched || control.dirty) && control.invalid);
  }

  nombreGenerador(generador: Generador): string {
    return generador.razonSocial
      || generador.nombreFantasia
      || [generador.nombre, generador.apellido].filter(Boolean).join(' ')
      || 'Generador sin nombre';
  }

  etiquetaTicket(ticket: TicketControl): string {
    return `Ticket #${ticket.numeroTicket ?? ticket.idTicket}`;
  }

  etiquetaHojaRuta(hoja: HojaRuta): string {
    return `HR-${hoja.numeroHojaRuta ?? hoja.id}`;
  }

  private generarManifiestoPendiente(): void {
    this.isSaving = true;
    this.ticketService.create({
      transportistaId: this.transportista!.idTransportista,
      hojaRutaId: this.hojaActual!.id,
      generadorId: Number(this.form.value.generadorId),
      fechaEmision: this.form.value.fechaEmision,
      estado: false,
      residuos: [],
    }).subscribe({
      next: (ticket) => {
        this.isSaving = false;
        const route = this.requestedHojaRutaId ? '/transportista/certificados' : '/home';
        void this.router.navigate([route], {
          queryParams: { manifiestoGenerado: ticket.numeroTicket ?? ticket.idTicket },
        });
      },
      error: (error) => {
        this.isSaving = false;
        this.message = this.getErrorMessage(error);
      },
    });
  }

  private completarManifiesto(): void {
    if (!this.ticketId) {
      return;
    }

    if (!this.tiposActivos.length) {
      this.message = 'No hay tipos de residuo activos para completar el manifiesto.';
      return;
    }

    const residuos = this.buildResiduos();

    if (!residuos.length) {
      this.message = 'Agrega al menos un residuo con kilaje para procesar el manifiesto.';
      return;
    }

    this.isSaving = true;
    this.ticketService.update(this.ticketId, {
      transportistaId: this.transportista!.idTransportista,
      hojaRutaId: this.hojaActual!.id,
      generadorId: Number(this.form.value.generadorId),
      fechaEmision: this.form.value.fechaEmision,
      estado: true,
      residuos,
    }).subscribe({
      next: () => {
        this.isSaving = false;
        void this.router.navigate([this.returnTo === 'certificados' ? '/transportista/certificados' : '/home'], {
          queryParams: { manifiestoProcesado: this.ticketActual?.numeroTicket ?? this.ticketId },
        });
      },
      error: (error) => {
        this.isSaving = false;
        this.message = this.getErrorMessage(error);
      },
    });
  }

  private loadTicket(id: number): void {
    this.ticketService.getById(id).subscribe({
      next: (ticket) => {
        this.ticketActual = ticket;
        this.loadHojaRutaDelTicket(ticket);
      },
      error: (error) => {
        this.isLoading = false;
        this.message = this.getErrorMessage(error);
      },
    });
  }

  private loadHojaRutaDelTicket(ticket: TicketControl): void {
    this.hojaRutaService.getById(ticket.hojaRutaId).subscribe({
      next: (hoja) => {
        this.hojaActual = hoja;
        this.patchTicket(ticket);
        this.isLoading = false;
      },
      error: () => {
        this.patchTicket(ticket);
        this.isLoading = false;
        this.message = 'No se pudo cargar la hoja de ruta del manifiesto.';
      },
    });
  }

  private patchTicket(ticket: TicketControl): void {
    this.form.patchValue({
      generadorId: ticket.generadorId,
      fechaEmision: ticket.fechaEmision,
    });
    this.patchResiduos(ticket);
  }

  private patchResiduos(ticket: TicketControl): void {
    this.residuos.clear();

    for (const residuo of ticket.residuos ?? []) {
      this.residuos.push(this.fb.group({
        tipoResiduoId: [residuo.tipoResiduoId, Validators.required],
        peso: [residuo.peso, [Validators.required, Validators.min(0.001)]],
      }));
    }

    if (!this.residuos.length) {
      this.addResiduo();
    }
  }

  private applyDashboardData(data: TransportistaDashboardData): void {
    this.generadores = data.generadoresActivos.filter((generador) => generador.estado);

    if (this.generadores.length && !this.form.get('generadorId')?.value) {
      this.form.patchValue({ generadorId: this.generadores[0].id });
    }

    if (this.isEditMode) {
      this.loadTiposActivos();
      return;
    }

    if (this.requestedHojaRutaId) {
      this.loadRequestedHojaRuta(this.requestedHojaRutaId);
      return;
    }

    this.hojaActual = data.hojaActual;

    if (this.hojaActual && !this.form.get('fechaEmision')?.value) {
      this.form.patchValue({ fechaEmision: this.hojaActual.fechaInicio });
    }

    this.loadTiposActivos();
  }

  private loadRequestedHojaRuta(id: number): void {
    this.hojaRutaService.getById(id).subscribe({
      next: (hoja) => {
        this.hojaActual = hoja;

        if (!this.form.get('fechaEmision')?.value) {
          this.form.patchValue({ fechaEmision: hoja.fechaInicio });
        }

        this.loadTiposActivos();
      },
      error: () => {
        this.hojaActual = null;
        this.message = 'No se pudo cargar la hoja de ruta seleccionada.';
        this.loadTiposActivos();
      },
    });
  }

  private buildResiduos(): ResiduoRequest[] {
    return this.residuos.controls
      .map((control) => ({
        tipoResiduoId: Number(control.get('tipoResiduoId')?.value),
        peso: Number(control.get('peso')?.value),
      }))
      .filter((residuo) => Boolean(residuo.tipoResiduoId) && residuo.peso > 0);
  }

  private fechaDentroDeHoja(value: string): boolean {
    if (!this.hojaActual || !value) {
      return false;
    }

    return value >= this.hojaActual.fechaInicio && value <= this.hojaActual.fechaFin;
  }

  private getErrorMessage(error: unknown): string {
    return (error as { error?: { message?: string } })?.error?.message
      ?? 'No se pudo guardar el manifiesto.';
  }
}
