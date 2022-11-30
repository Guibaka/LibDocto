import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'slot',
        data: { pageTitle: 'Slots' },
        loadChildren: () => import('./slot/slot.module').then(m => m.SlotModule),
      },
      {
        path: 'calendar',
        data: { pageTitle: 'Calendars' },
        loadChildren: () => import('./calendar/calendar.module').then(m => m.CalendarModule),
      },
      {
        path: 'doctor',
        data: { pageTitle: 'Doctors' },
        loadChildren: () => import('./doctor/doctor.module').then(m => m.DoctorModule),
      },
      {
        path: 'client-connect',
        data: { pageTitle: 'ClientConnects' },
        loadChildren: () => import('./client-connect/client-connect.module').then(m => m.ClientConnectModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
