import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ISlot } from '../slot.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../slot.test-samples';

import { SlotService, RestSlot } from './slot.service';

const requireRestSample: RestSlot = {
  ...sampleWithRequiredData,
  timeStart: sampleWithRequiredData.timeStart?.format(DATE_FORMAT),
  timeEnd: sampleWithRequiredData.timeEnd?.format(DATE_FORMAT),
};

describe('Slot Service', () => {
  let service: SlotService;
  let httpMock: HttpTestingController;
  let expectedResult: ISlot | ISlot[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SlotService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Slot', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const slot = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(slot).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Slot', () => {
      const slot = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(slot).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Slot', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Slot', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Slot', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSlotToCollectionIfMissing', () => {
      it('should add a Slot to an empty array', () => {
        const slot: ISlot = sampleWithRequiredData;
        expectedResult = service.addSlotToCollectionIfMissing([], slot);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(slot);
      });

      it('should not add a Slot to an array that contains it', () => {
        const slot: ISlot = sampleWithRequiredData;
        const slotCollection: ISlot[] = [
          {
            ...slot,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSlotToCollectionIfMissing(slotCollection, slot);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Slot to an array that doesn't contain it", () => {
        const slot: ISlot = sampleWithRequiredData;
        const slotCollection: ISlot[] = [sampleWithPartialData];
        expectedResult = service.addSlotToCollectionIfMissing(slotCollection, slot);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(slot);
      });

      it('should add only unique Slot to an array', () => {
        const slotArray: ISlot[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const slotCollection: ISlot[] = [sampleWithRequiredData];
        expectedResult = service.addSlotToCollectionIfMissing(slotCollection, ...slotArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const slot: ISlot = sampleWithRequiredData;
        const slot2: ISlot = sampleWithPartialData;
        expectedResult = service.addSlotToCollectionIfMissing([], slot, slot2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(slot);
        expect(expectedResult).toContain(slot2);
      });

      it('should accept null and undefined values', () => {
        const slot: ISlot = sampleWithRequiredData;
        expectedResult = service.addSlotToCollectionIfMissing([], null, slot, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(slot);
      });

      it('should return initial array if no Slot is added', () => {
        const slotCollection: ISlot[] = [sampleWithRequiredData];
        expectedResult = service.addSlotToCollectionIfMissing(slotCollection, undefined, null);
        expect(expectedResult).toEqual(slotCollection);
      });
    });

    describe('compareSlot', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSlot(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSlot(entity1, entity2);
        const compareResult2 = service.compareSlot(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSlot(entity1, entity2);
        const compareResult2 = service.compareSlot(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSlot(entity1, entity2);
        const compareResult2 = service.compareSlot(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
