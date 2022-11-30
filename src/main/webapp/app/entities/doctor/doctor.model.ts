import dayjs from 'dayjs/esm';
import { ICalendar } from 'app/entities/calendar/calendar.model';

export interface IDoctor {
  id: number;
  idDoctor?: number | null;
  firstName?: string | null;
  lastName?: string | null;
  mail?: string | null;
  address?: string | null;
  phone?: string | null;
  scheduleStart?: dayjs.Dayjs | null;
  scheduletEnd?: dayjs.Dayjs | null;
  calendar?: Pick<ICalendar, 'id'> | null;
}

export type NewDoctor = Omit<IDoctor, 'id'> & { id: null };
