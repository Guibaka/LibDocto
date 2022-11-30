import dayjs from 'dayjs/esm';

import { ICalendar, NewCalendar } from './calendar.model';

export const sampleWithRequiredData: ICalendar = {
  id: 70069,
  idCalender: 69589,
};

export const sampleWithPartialData: ICalendar = {
  id: 70714,
  idCalender: 34791,
  timeStart: dayjs('2022-11-30'),
  timeEnd: dayjs('2022-11-30'),
};

export const sampleWithFullData: ICalendar = {
  id: 86614,
  idCalender: 29094,
  timeStart: dayjs('2022-11-30'),
  timeEnd: dayjs('2022-11-30'),
};

export const sampleWithNewData: NewCalendar = {
  idCalender: 77559,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
