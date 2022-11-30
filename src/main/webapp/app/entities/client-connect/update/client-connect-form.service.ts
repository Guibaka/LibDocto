import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IClientConnect, NewClientConnect } from '../client-connect.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClientConnect for edit and NewClientConnectFormGroupInput for create.
 */
type ClientConnectFormGroupInput = IClientConnect | PartialWithRequiredKeyOf<NewClientConnect>;

type ClientConnectFormDefaults = Pick<NewClientConnect, 'id'>;

type ClientConnectFormGroupContent = {
  id: FormControl<IClientConnect['id'] | NewClientConnect['id']>;
  idClientConnect: FormControl<IClientConnect['idClientConnect']>;
  firstName: FormControl<IClientConnect['firstName']>;
  lastName: FormControl<IClientConnect['lastName']>;
  mail: FormControl<IClientConnect['mail']>;
  password: FormControl<IClientConnect['password']>;
};

export type ClientConnectFormGroup = FormGroup<ClientConnectFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClientConnectFormService {
  createClientConnectFormGroup(clientConnect: ClientConnectFormGroupInput = { id: null }): ClientConnectFormGroup {
    const clientConnectRawValue = {
      ...this.getFormDefaults(),
      ...clientConnect,
    };
    return new FormGroup<ClientConnectFormGroupContent>({
      id: new FormControl(
        { value: clientConnectRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      idClientConnect: new FormControl(clientConnectRawValue.idClientConnect, {
        validators: [Validators.required],
      }),
      firstName: new FormControl(clientConnectRawValue.firstName),
      lastName: new FormControl(clientConnectRawValue.lastName),
      mail: new FormControl(clientConnectRawValue.mail),
      password: new FormControl(clientConnectRawValue.password),
    });
  }

  getClientConnect(form: ClientConnectFormGroup): IClientConnect | NewClientConnect {
    return form.getRawValue() as IClientConnect | NewClientConnect;
  }

  resetForm(form: ClientConnectFormGroup, clientConnect: ClientConnectFormGroupInput): void {
    const clientConnectRawValue = { ...this.getFormDefaults(), ...clientConnect };
    form.reset(
      {
        ...clientConnectRawValue,
        id: { value: clientConnectRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ClientConnectFormDefaults {
    return {
      id: null,
    };
  }
}
