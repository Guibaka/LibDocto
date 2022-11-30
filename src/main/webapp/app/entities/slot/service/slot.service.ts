import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISlot, NewSlot } from '../slot.model';

export type PartialUpdateSlot = Partial<ISlot> & Pick<ISlot, 'id'>;

type RestOf<T extends ISlot | NewSlot> = Omit<T, 'timeStart' | 'timeEnd'> & {
  timeStart?: string | null;
  timeEnd?: string | null;
};

export type RestSlot = RestOf<ISlot>;

export type NewRestSlot = RestOf<NewSlot>;

export type PartialUpdateRestSlot = RestOf<PartialUpdateSlot>;

export type EntityResponseType = HttpResponse<ISlot>;
export type EntityArrayResponseType = HttpResponse<ISlot[]>;

@Injectable({ providedIn: 'root' })
export class SlotService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/slots');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(slot: NewSlot): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(slot);
    return this.http.post<RestSlot>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(slot: ISlot): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(slot);
    return this.http
      .put<RestSlot>(`${this.resourceUrl}/${this.getSlotIdentifier(slot)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(slot: PartialUpdateSlot): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(slot);
    return this.http
      .patch<RestSlot>(`${this.resourceUrl}/${this.getSlotIdentifier(slot)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSlot>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSlot[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSlotIdentifier(slot: Pick<ISlot, 'id'>): number {
    return slot.id;
  }

  compareSlot(o1: Pick<ISlot, 'id'> | null, o2: Pick<ISlot, 'id'> | null): boolean {
    return o1 && o2 ? this.getSlotIdentifier(o1) === this.getSlotIdentifier(o2) : o1 === o2;
  }

  addSlotToCollectionIfMissing<Type extends Pick<ISlot, 'id'>>(
    slotCollection: Type[],
    ...slotsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const slots: Type[] = slotsToCheck.filter(isPresent);
    if (slots.length > 0) {
      const slotCollectionIdentifiers = slotCollection.map(slotItem => this.getSlotIdentifier(slotItem)!);
      const slotsToAdd = slots.filter(slotItem => {
        const slotIdentifier = this.getSlotIdentifier(slotItem);
        if (slotCollectionIdentifiers.includes(slotIdentifier)) {
          return false;
        }
        slotCollectionIdentifiers.push(slotIdentifier);
        return true;
      });
      return [...slotsToAdd, ...slotCollection];
    }
    return slotCollection;
  }

  protected convertDateFromClient<T extends ISlot | NewSlot | PartialUpdateSlot>(slot: T): RestOf<T> {
    return {
      ...slot,
      timeStart: slot.timeStart?.format(DATE_FORMAT) ?? null,
      timeEnd: slot.timeEnd?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restSlot: RestSlot): ISlot {
    return {
      ...restSlot,
      timeStart: restSlot.timeStart ? dayjs(restSlot.timeStart) : undefined,
      timeEnd: restSlot.timeEnd ? dayjs(restSlot.timeEnd) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSlot>): HttpResponse<ISlot> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSlot[]>): HttpResponse<ISlot[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
