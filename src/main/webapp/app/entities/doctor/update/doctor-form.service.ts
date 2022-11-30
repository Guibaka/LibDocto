import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IDoctor, NewDoctor } from '../doctor.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDoctor for edit and NewDoctorFormGroupInput for create.
 */
type DoctorFormGroupInput = IDoctor | PartialWithRequiredKeyOf<NewDoctor>;

type DoctorFormDefaults = Pick<NewDoctor, 'id'>;

type DoctorFormGroupContent = {
  id: FormControl<IDoctor['id'] | NewDoctor['id']>;
  idDoctor: FormControl<IDoctor['idDoctor']>;
  firstName: FormControl<IDoctor['firstName']>;
  lastName: FormControl<IDoctor['lastName']>;
  mail: FormControl<IDoctor['mail']>;
  address: FormControl<IDoctor['address']>;
  phone: FormControl<IDoctor['phone']>;
  scheduleStart: FormControl<IDoctor['scheduleStart']>;
  scheduletEnd: FormControl<IDoctor['scheduletEnd']>;
  calendar: FormControl<IDoctor['calendar']>;
};

export type DoctorFormGroup = FormGroup<DoctorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DoctorFormService {
  createDoctorFormGroup(doctor: DoctorFormGroupInput = { id: null }): DoctorFormGroup {
    const doctorRawValue = {
      ...this.getFormDefaults(),
      ...doctor,
    };
    return new FormGroup<DoctorFormGroupContent>({
      id: new FormControl(
        { value: doctorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      idDoctor: new FormControl(doctorRawValue.idDoctor, {
        validators: [Validators.required],
      }),
      firstName: new FormControl(doctorRawValue.firstName),
      lastName: new FormControl(doctorRawValue.lastName),
      mail: new FormControl(doctorRawValue.mail),
      address: new FormControl(doctorRawValue.address),
      phone: new FormControl(doctorRawValue.phone),
      scheduleStart: new FormControl(doctorRawValue.scheduleStart),
      scheduletEnd: new FormControl(doctorRawValue.scheduletEnd),
      calendar: new FormControl(doctorRawValue.calendar),
    });
  }

  getDoctor(form: DoctorFormGroup): IDoctor | NewDoctor {
    return form.getRawValue() as IDoctor | NewDoctor;
  }

  resetForm(form: DoctorFormGroup, doctor: DoctorFormGroupInput): void {
    const doctorRawValue = { ...this.getFormDefaults(), ...doctor };
    form.reset(
      {
        ...doctorRawValue,
        id: { value: doctorRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DoctorFormDefaults {
    return {
      id: null,
    };
  }
}
