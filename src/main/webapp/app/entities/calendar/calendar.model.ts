import dayjs from 'dayjs/esm';

export interface ICalendar {
  id: number;
  idCalender?: number | null;
  timeStart?: dayjs.Dayjs | null;
  timeEnd?: dayjs.Dayjs | null;
}

export type NewCalendar = Omit<ICalendar, 'id'> & { id: null };
