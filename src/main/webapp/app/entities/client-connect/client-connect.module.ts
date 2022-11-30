import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ClientConnectComponent } from './list/client-connect.component';
import { ClientConnectDetailComponent } from './detail/client-connect-detail.component';
import { ClientConnectUpdateComponent } from './update/client-connect-update.component';
import { ClientConnectDeleteDialogComponent } from './delete/client-connect-delete-dialog.component';
import { ClientConnectRoutingModule } from './route/client-connect-routing.module';

@NgModule({
  imports: [SharedModule, ClientConnectRoutingModule],
  declarations: [ClientConnectComponent, ClientConnectDetailComponent, ClientConnectUpdateComponent, ClientConnectDeleteDialogComponent],
})
export class ClientConnectModule {}
