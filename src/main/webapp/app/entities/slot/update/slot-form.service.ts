import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISlot, NewSlot } from '../slot.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISlot for edit and NewSlotFormGroupInput for create.
 */
type SlotFormGroupInput = ISlot | PartialWithRequiredKeyOf<NewSlot>;

type SlotFormDefaults = Pick<NewSlot, 'id'>;

type SlotFormGroupContent = {
  id: FormControl<ISlot['id'] | NewSlot['id']>;
  idAppointment: FormControl<ISlot['idAppointment']>;
  availability: FormControl<ISlot['availability']>;
  timeStart: FormControl<ISlot['timeStart']>;
  timeEnd: FormControl<ISlot['timeEnd']>;
  doctor: FormControl<ISlot['doctor']>;
  clientConnect: FormControl<ISlot['clientConnect']>;
  calendar: FormControl<ISlot['calendar']>;
};

export type SlotFormGroup = FormGroup<SlotFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SlotFormService {
  createSlotFormGroup(slot: SlotFormGroupInput = { id: null }): SlotFormGroup {
    const slotRawValue = {
      ...this.getFormDefaults(),
      ...slot,
    };
    return new FormGroup<SlotFormGroupContent>({
      id: new FormControl(
        { value: slotRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      idAppointment: new FormControl(slotRawValue.idAppointment, {
        validators: [Validators.required],
      }),
      availability: new FormControl(slotRawValue.availability),
      timeStart: new FormControl(slotRawValue.timeStart),
      timeEnd: new FormControl(slotRawValue.timeEnd),
      doctor: new FormControl(slotRawValue.doctor),
      clientConnect: new FormControl(slotRawValue.clientConnect),
      calendar: new FormControl(slotRawValue.calendar),
    });
  }

  getSlot(form: SlotFormGroup): ISlot | NewSlot {
    return form.getRawValue() as ISlot | NewSlot;
  }

  resetForm(form: SlotFormGroup, slot: SlotFormGroupInput): void {
    const slotRawValue = { ...this.getFormDefaults(), ...slot };
    form.reset(
      {
        ...slotRawValue,
        id: { value: slotRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SlotFormDefaults {
    return {
      id: null,
    };
  }
}
