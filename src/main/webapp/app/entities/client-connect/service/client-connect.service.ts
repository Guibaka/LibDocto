import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IClientConnect, NewClientConnect } from '../client-connect.model';

export type PartialUpdateClientConnect = Partial<IClientConnect> & Pick<IClientConnect, 'id'>;

export type EntityResponseType = HttpResponse<IClientConnect>;
export type EntityArrayResponseType = HttpResponse<IClientConnect[]>;

@Injectable({ providedIn: 'root' })
export class ClientConnectService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/client-connects');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(clientConnect: NewClientConnect): Observable<EntityResponseType> {
    return this.http.post<IClientConnect>(this.resourceUrl, clientConnect, { observe: 'response' });
  }

  update(clientConnect: IClientConnect): Observable<EntityResponseType> {
    return this.http.put<IClientConnect>(`${this.resourceUrl}/${this.getClientConnectIdentifier(clientConnect)}`, clientConnect, {
      observe: 'response',
    });
  }

  partialUpdate(clientConnect: PartialUpdateClientConnect): Observable<EntityResponseType> {
    return this.http.patch<IClientConnect>(`${this.resourceUrl}/${this.getClientConnectIdentifier(clientConnect)}`, clientConnect, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IClientConnect>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClientConnect[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getClientConnectIdentifier(clientConnect: Pick<IClientConnect, 'id'>): number {
    return clientConnect.id;
  }

  compareClientConnect(o1: Pick<IClientConnect, 'id'> | null, o2: Pick<IClientConnect, 'id'> | null): boolean {
    return o1 && o2 ? this.getClientConnectIdentifier(o1) === this.getClientConnectIdentifier(o2) : o1 === o2;
  }

  addClientConnectToCollectionIfMissing<Type extends Pick<IClientConnect, 'id'>>(
    clientConnectCollection: Type[],
    ...clientConnectsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const clientConnects: Type[] = clientConnectsToCheck.filter(isPresent);
    if (clientConnects.length > 0) {
      const clientConnectCollectionIdentifiers = clientConnectCollection.map(
        clientConnectItem => this.getClientConnectIdentifier(clientConnectItem)!
      );
      const clientConnectsToAdd = clientConnects.filter(clientConnectItem => {
        const clientConnectIdentifier = this.getClientConnectIdentifier(clientConnectItem);
        if (clientConnectCollectionIdentifiers.includes(clientConnectIdentifier)) {
          return false;
        }
        clientConnectCollectionIdentifiers.push(clientConnectIdentifier);
        return true;
      });
      return [...clientConnectsToAdd, ...clientConnectCollection];
    }
    return clientConnectCollection;
  }
}
