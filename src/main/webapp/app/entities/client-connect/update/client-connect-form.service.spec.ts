import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../client-connect.test-samples';

import { ClientConnectFormService } from './client-connect-form.service';

describe('ClientConnect Form Service', () => {
  let service: ClientConnectFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClientConnectFormService);
  });

  describe('Service methods', () => {
    describe('createClientConnectFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createClientConnectFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            idClientConnect: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            mail: expect.any(Object),
            password: expect.any(Object),
          })
        );
      });

      it('passing IClientConnect should create a new form with FormGroup', () => {
        const formGroup = service.createClientConnectFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            idClientConnect: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            mail: expect.any(Object),
            password: expect.any(Object),
          })
        );
      });
    });

    describe('getClientConnect', () => {
      it('should return NewClientConnect for default ClientConnect initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createClientConnectFormGroup(sampleWithNewData);

        const clientConnect = service.getClientConnect(formGroup) as any;

        expect(clientConnect).toMatchObject(sampleWithNewData);
      });

      it('should return NewClientConnect for empty ClientConnect initial value', () => {
        const formGroup = service.createClientConnectFormGroup();

        const clientConnect = service.getClientConnect(formGroup) as any;

        expect(clientConnect).toMatchObject({});
      });

      it('should return IClientConnect', () => {
        const formGroup = service.createClientConnectFormGroup(sampleWithRequiredData);

        const clientConnect = service.getClientConnect(formGroup) as any;

        expect(clientConnect).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IClientConnect should not enable id FormControl', () => {
        const formGroup = service.createClientConnectFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewClientConnect should disable id FormControl', () => {
        const formGroup = service.createClientConnectFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
