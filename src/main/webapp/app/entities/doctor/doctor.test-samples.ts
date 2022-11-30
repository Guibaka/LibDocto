import dayjs from 'dayjs/esm';

import { IDoctor, NewDoctor } from './doctor.model';

export const sampleWithRequiredData: IDoctor = {
  id: 47308,
  idDoctor: 56416,
};

export const sampleWithPartialData: IDoctor = {
  id: 86369,
  idDoctor: 53768,
  lastName: 'Herzog',
  mail: 'hack mint',
};

export const sampleWithFullData: IDoctor = {
  id: 61958,
  idDoctor: 40381,
  firstName: 'Francis',
  lastName: 'Feeney',
  mail: 'ivory Borders Clothing',
  address: 'Electronics Dollar payment',
  phone: '(902) 928-1548 x997',
  scheduleStart: dayjs('2022-11-30'),
  scheduletEnd: dayjs('2022-11-30'),
};

export const sampleWithNewData: NewDoctor = {
  idDoctor: 72932,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
