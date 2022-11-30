import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDoctor, NewDoctor } from '../doctor.model';

export type PartialUpdateDoctor = Partial<IDoctor> & Pick<IDoctor, 'id'>;

type RestOf<T extends IDoctor | NewDoctor> = Omit<T, 'scheduleStart' | 'scheduletEnd'> & {
  scheduleStart?: string | null;
  scheduletEnd?: string | null;
};

export type RestDoctor = RestOf<IDoctor>;

export type NewRestDoctor = RestOf<NewDoctor>;

export type PartialUpdateRestDoctor = RestOf<PartialUpdateDoctor>;

export type EntityResponseType = HttpResponse<IDoctor>;
export type EntityArrayResponseType = HttpResponse<IDoctor[]>;

@Injectable({ providedIn: 'root' })
export class DoctorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/doctors');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(doctor: NewDoctor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(doctor);
    return this.http
      .post<RestDoctor>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(doctor: IDoctor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(doctor);
    return this.http
      .put<RestDoctor>(`${this.resourceUrl}/${this.getDoctorIdentifier(doctor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(doctor: PartialUpdateDoctor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(doctor);
    return this.http
      .patch<RestDoctor>(`${this.resourceUrl}/${this.getDoctorIdentifier(doctor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDoctor>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDoctor[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDoctorIdentifier(doctor: Pick<IDoctor, 'id'>): number {
    return doctor.id;
  }

  compareDoctor(o1: Pick<IDoctor, 'id'> | null, o2: Pick<IDoctor, 'id'> | null): boolean {
    return o1 && o2 ? this.getDoctorIdentifier(o1) === this.getDoctorIdentifier(o2) : o1 === o2;
  }

  addDoctorToCollectionIfMissing<Type extends Pick<IDoctor, 'id'>>(
    doctorCollection: Type[],
    ...doctorsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const doctors: Type[] = doctorsToCheck.filter(isPresent);
    if (doctors.length > 0) {
      const doctorCollectionIdentifiers = doctorCollection.map(doctorItem => this.getDoctorIdentifier(doctorItem)!);
      const doctorsToAdd = doctors.filter(doctorItem => {
        const doctorIdentifier = this.getDoctorIdentifier(doctorItem);
        if (doctorCollectionIdentifiers.includes(doctorIdentifier)) {
          return false;
        }
        doctorCollectionIdentifiers.push(doctorIdentifier);
        return true;
      });
      return [...doctorsToAdd, ...doctorCollection];
    }
    return doctorCollection;
  }

  protected convertDateFromClient<T extends IDoctor | NewDoctor | PartialUpdateDoctor>(doctor: T): RestOf<T> {
    return {
      ...doctor,
      scheduleStart: doctor.scheduleStart?.format(DATE_FORMAT) ?? null,
      scheduletEnd: doctor.scheduletEnd?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restDoctor: RestDoctor): IDoctor {
    return {
      ...restDoctor,
      scheduleStart: restDoctor.scheduleStart ? dayjs(restDoctor.scheduleStart) : undefined,
      scheduletEnd: restDoctor.scheduletEnd ? dayjs(restDoctor.scheduletEnd) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDoctor>): HttpResponse<IDoctor> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDoctor[]>): HttpResponse<IDoctor[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
