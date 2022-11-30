import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICalendar, NewCalendar } from '../calendar.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICalendar for edit and NewCalendarFormGroupInput for create.
 */
type CalendarFormGroupInput = ICalendar | PartialWithRequiredKeyOf<NewCalendar>;

type CalendarFormDefaults = Pick<NewCalendar, 'id'>;

type CalendarFormGroupContent = {
  id: FormControl<ICalendar['id'] | NewCalendar['id']>;
  idCalender: FormControl<ICalendar['idCalender']>;
  timeStart: FormControl<ICalendar['timeStart']>;
  timeEnd: FormControl<ICalendar['timeEnd']>;
};

export type CalendarFormGroup = FormGroup<CalendarFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CalendarFormService {
  createCalendarFormGroup(calendar: CalendarFormGroupInput = { id: null }): CalendarFormGroup {
    const calendarRawValue = {
      ...this.getFormDefaults(),
      ...calendar,
    };
    return new FormGroup<CalendarFormGroupContent>({
      id: new FormControl(
        { value: calendarRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      idCalender: new FormControl(calendarRawValue.idCalender, {
        validators: [Validators.required],
      }),
      timeStart: new FormControl(calendarRawValue.timeStart),
      timeEnd: new FormControl(calendarRawValue.timeEnd),
    });
  }

  getCalendar(form: CalendarFormGroup): ICalendar | NewCalendar {
    return form.getRawValue() as ICalendar | NewCalendar;
  }

  resetForm(form: CalendarFormGroup, calendar: CalendarFormGroupInput): void {
    const calendarRawValue = { ...this.getFormDefaults(), ...calendar };
    form.reset(
      {
        ...calendarRawValue,
        id: { value: calendarRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CalendarFormDefaults {
    return {
      id: null,
    };
  }
}
