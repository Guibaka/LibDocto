import dayjs from 'dayjs/esm';

import { StateSlot } from 'app/entities/enumerations/state-slot.model';

import { ISlot, NewSlot } from './slot.model';

export const sampleWithRequiredData: ISlot = {
  id: 88479,
  idAppointment: 7338,
};

export const sampleWithPartialData: ISlot = {
  id: 52202,
  idAppointment: 74687,
  availability: StateSlot['BOOKED'],
  timeEnd: dayjs('2022-11-29'),
};

export const sampleWithFullData: ISlot = {
  id: 26825,
  idAppointment: 9156,
  availability: StateSlot['BOOKED'],
  timeStart: dayjs('2022-11-30'),
  timeEnd: dayjs('2022-11-30'),
};

export const sampleWithNewData: NewSlot = {
  idAppointment: 15848,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
