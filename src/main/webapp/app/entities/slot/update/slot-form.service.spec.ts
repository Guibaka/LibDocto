import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../slot.test-samples';

import { SlotFormService } from './slot-form.service';

describe('Slot Form Service', () => {
  let service: SlotFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SlotFormService);
  });

  describe('Service methods', () => {
    describe('createSlotFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSlotFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            idAppointment: expect.any(Object),
            availability: expect.any(Object),
            timeStart: expect.any(Object),
            timeEnd: expect.any(Object),
            doctor: expect.any(Object),
            clientConnect: expect.any(Object),
            calendar: expect.any(Object),
          })
        );
      });

      it('passing ISlot should create a new form with FormGroup', () => {
        const formGroup = service.createSlotFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            idAppointment: expect.any(Object),
            availability: expect.any(Object),
            timeStart: expect.any(Object),
            timeEnd: expect.any(Object),
            doctor: expect.any(Object),
            clientConnect: expect.any(Object),
            calendar: expect.any(Object),
          })
        );
      });
    });

    describe('getSlot', () => {
      it('should return NewSlot for default Slot initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSlotFormGroup(sampleWithNewData);

        const slot = service.getSlot(formGroup) as any;

        expect(slot).toMatchObject(sampleWithNewData);
      });

      it('should return NewSlot for empty Slot initial value', () => {
        const formGroup = service.createSlotFormGroup();

        const slot = service.getSlot(formGroup) as any;

        expect(slot).toMatchObject({});
      });

      it('should return ISlot', () => {
        const formGroup = service.createSlotFormGroup(sampleWithRequiredData);

        const slot = service.getSlot(formGroup) as any;

        expect(slot).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISlot should not enable id FormControl', () => {
        const formGroup = service.createSlotFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSlot should disable id FormControl', () => {
        const formGroup = service.createSlotFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
