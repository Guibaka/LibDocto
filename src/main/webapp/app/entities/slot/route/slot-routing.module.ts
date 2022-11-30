import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SlotComponent } from '../list/slot.component';
import { SlotDetailComponent } from '../detail/slot-detail.component';
import { SlotUpdateComponent } from '../update/slot-update.component';
import { SlotRoutingResolveService } from './slot-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const slotRoute: Routes = [
  {
    path: '',
    component: SlotComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SlotDetailComponent,
    resolve: {
      slot: SlotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SlotUpdateComponent,
    resolve: {
      slot: SlotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SlotUpdateComponent,
    resolve: {
      slot: SlotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(slotRoute)],
  exports: [RouterModule],
})
export class SlotRoutingModule {}
