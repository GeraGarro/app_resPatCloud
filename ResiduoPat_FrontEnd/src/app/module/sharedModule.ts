import { NgModule } from '@angular/core';
import { PesoPipe } from '../../assets/pipe/peso'

@NgModule({
  declarations: [PesoPipe],
  exports: [PesoPipe]
})
export class SharedModule { }