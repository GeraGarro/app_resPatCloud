import { Component, Inject, Input, OnInit } from '@angular/core';
import { MatDialogRef,MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ResiduoDTO } from 'src/app/services/models/residuoDTO';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { ViewChild } from '@angular/core';
@Component({
  selector: 'app-residuo-modal',
  templateUrl: './residuo-modal.component.html',
  styleUrls: ['./residuo-modal.component.css']
})
export class ResiduoModalComponent implements OnInit{

dataSource!: MatTableDataSource<ResiduoDTO>;

displayedColumns: string[] = ['tipo_residuo', 'peso','Acciones'];

constructor(public DialogRef: MatDialogRef<ResiduoModalComponent>, @Inject(MAT_DIALOG_DATA) public data: { residuos: ResiduoDTO[] }){

}


  ngOnInit(): void {
    this.dataSource = new MatTableDataSource(this.data.residuos);
    
    this.dataSource.connect();
  }

  editar(element: ResiduoDTO){
    console.log(element)
  }
close():void{
  this.DialogRef.close();
}

}
