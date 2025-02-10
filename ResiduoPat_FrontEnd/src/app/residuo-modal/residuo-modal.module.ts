import { NgModule } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { ResiduoModalComponent } from './residuo-modal.component';

@NgModule({
  declarations: [
    ResiduoModalComponent,
  ],
  imports: [
    MatTableModule,
    MatCardModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatSelectModule,
    CommonModule,
    MatButtonModule
  ],
})
export class residuoModalModule {}