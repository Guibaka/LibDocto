import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SlotComponent } from './list/slot.component';
import { SlotDetailComponent } from './detail/slot-detail.component';
import { SlotUpdateComponent } from './update/slot-update.component';
import { SlotDeleteDialogComponent } from './delete/slot-delete-dialog.component';
import { SlotRoutingModule } from './route/slot-routing.module';

@NgModule({
  imports: [SharedModule, SlotRoutingModule],
  declarations: [SlotComponent, SlotDetailComponent, SlotUpdateComponent, SlotDeleteDialogComponent],
})
export class SlotModule {}
