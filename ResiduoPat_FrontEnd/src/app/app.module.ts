import { LOCALE_ID, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TransportistaComponent } from './transportista/transportista.component';

import { TransportistaFormularioComponent } from './transportista-formulario/transportista-formulario.component';
import{HttpClientModule} from '@angular/common/http';
import {MatCardModule} from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';


import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import localeEs from '@angular/common/locales/es-AR'
import { registerLocaleData } from '@angular/common';
import { MatDialogModule } from '@angular/material/dialog';
import{residuoModalModule} from './residuo-modal/residuo-modal.module';

import { DateFnsAdapter } from '@angular/material-date-fns-adapter';
import{es} from 'date-fns/locale';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, MatDateFormats } from '@angular/material/core';


import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { ConfirmacionDialogoComponent } from './confirmacion-dialogo/confirmacion-dialogo.component';
import { MatButtonModule } from '@angular/material/button';

registerLocaleData(localeEs, 'es');

export const Date_Formats: MatDateFormats={
  parse:{ dateInput:'dd-MM-yyyy'},
  display:{
    dateInput:'dd-MM-yyyy',
    monthYearLabel:'MM yyyy',
    dateA11yLabel: 'LL',
    monthYearA11yLabel:'yyyy'
  }
}

registerLocaleData(localeEs,'es');

@NgModule({
  declarations: [
    AppComponent,
    TransportistaComponent,
    TransportistaFormularioComponent,
    ConfirmacionDialogoComponent,
   

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    MatCardModule,
    BrowserAnimationsModule,
    NoopAnimationsModule,
    MatDialogModule,
    residuoModalModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDialogModule,
    MatButtonModule,

  ],
  providers: [{ provide: LOCALE_ID, useValue: 'es' },{provide:localeEs,useValue:'es'},{provide:DateAdapter, useClass:DateFnsAdapter},{provide:MAT_DATE_FORMATS,useValue:Date_Formats},{provide:MAT_DATE_LOCALE,useValue:es}],
  bootstrap: [AppComponent]
})

export class AppModule { }
