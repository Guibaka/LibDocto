import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ClientConnectComponent } from '../list/client-connect.component';
import { ClientConnectDetailComponent } from '../detail/client-connect-detail.component';
import { ClientConnectUpdateComponent } from '../update/client-connect-update.component';
import { ClientConnectRoutingResolveService } from './client-connect-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const clientConnectRoute: Routes = [
  {
    path: '',
    component: ClientConnectComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ClientConnectDetailComponent,
    resolve: {
      clientConnect: ClientConnectRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ClientConnectUpdateComponent,
    resolve: {
      clientConnect: ClientConnectRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ClientConnectUpdateComponent,
    resolve: {
      clientConnect: ClientConnectRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(clientConnectRoute)],
  exports: [RouterModule],
})
export class ClientConnectRoutingModule {}
