import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISlot } from '../slot.model';
import { SlotService } from '../service/slot.service';

@Injectable({ providedIn: 'root' })
export class SlotRoutingResolveService implements Resolve<ISlot | null> {
  constructor(protected service: SlotService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISlot | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((slot: HttpResponse<ISlot>) => {
          if (slot.body) {
            return of(slot.body);
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
