import dayjs from 'dayjs/esm';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { IClientConnect } from 'app/entities/client-connect/client-connect.model';
import { ICalendar } from 'app/entities/calendar/calendar.model';
import { StateSlot } from 'app/entities/enumerations/state-slot.model';

export interface ISlot {
  id: number;
  idAppointment?: number | null;
  availability?: StateSlot | null;
  timeStart?: dayjs.Dayjs | null;
  timeEnd?: dayjs.Dayjs | null;
  doctor?: Pick<IDoctor, 'id'> | null;
  clientConnect?: Pick<IClientConnect, 'id'> | null;
  calendar?: Pick<ICalendar, 'id'> | null;
}

export type NewSlot = Omit<ISlot, 'id'> & { id: null };
