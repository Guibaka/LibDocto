import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDoctor } from '../doctor.model';
import { DoctorService } from '../service/doctor.service';

@Injectable({ providedIn: 'root' })
export class DoctorRoutingResolveService implements Resolve<IDoctor | null> {
  constructor(protected service: DoctorService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDoctor | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((doctor: HttpResponse<IDoctor>) => {
          if (doctor.body) {
            return of(doctor.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
