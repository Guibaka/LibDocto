import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IClientConnect } from '../client-connect.model';
import { ClientConnectService } from '../service/client-connect.service';

@Injectable({ providedIn: 'root' })
export class ClientConnectRoutingResolveService implements Resolve<IClientConnect | null> {
  constructor(protected service: ClientConnectService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IClientConnect | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((clientConnect: HttpResponse<IClientConnect>) => {
          if (clientConnect.body) {
            return of(clientConnect.body);
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
