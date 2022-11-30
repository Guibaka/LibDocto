import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IClientConnect } from '../client-connect.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../client-connect.test-samples';

import { ClientConnectService } from './client-connect.service';

const requireRestSample: IClientConnect = {
  ...sampleWithRequiredData,
};

describe('ClientConnect Service', () => {
  let service: ClientConnectService;
  let httpMock: HttpTestingController;
  let expectedResult: IClientConnect | IClientConnect[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ClientConnectService);
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

    it('should create a ClientConnect', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const clientConnect = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(clientConnect).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ClientConnect', () => {
      const clientConnect = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(clientConnect).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ClientConnect', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ClientConnect', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ClientConnect', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addClientConnectToCollectionIfMissing', () => {
      it('should add a ClientConnect to an empty array', () => {
        const clientConnect: IClientConnect = sampleWithRequiredData;
        expectedResult = service.addClientConnectToCollectionIfMissing([], clientConnect);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientConnect);
      });

      it('should not add a ClientConnect to an array that contains it', () => {
        const clientConnect: IClientConnect = sampleWithRequiredData;
        const clientConnectCollection: IClientConnect[] = [
          {
            ...clientConnect,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addClientConnectToCollectionIfMissing(clientConnectCollection, clientConnect);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ClientConnect to an array that doesn't contain it", () => {
        const clientConnect: IClientConnect = sampleWithRequiredData;
        const clientConnectCollection: IClientConnect[] = [sampleWithPartialData];
        expectedResult = service.addClientConnectToCollectionIfMissing(clientConnectCollection, clientConnect);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientConnect);
      });

      it('should add only unique ClientConnect to an array', () => {
        const clientConnectArray: IClientConnect[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const clientConnectCollection: IClientConnect[] = [sampleWithRequiredData];
        expectedResult = service.addClientConnectToCollectionIfMissing(clientConnectCollection, ...clientConnectArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const clientConnect: IClientConnect = sampleWithRequiredData;
        const clientConnect2: IClientConnect = sampleWithPartialData;
        expectedResult = service.addClientConnectToCollectionIfMissing([], clientConnect, clientConnect2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientConnect);
        expect(expectedResult).toContain(clientConnect2);
      });

      it('should accept null and undefined values', () => {
        const clientConnect: IClientConnect = sampleWithRequiredData;
        expectedResult = service.addClientConnectToCollectionIfMissing([], null, clientConnect, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientConnect);
      });

      it('should return initial array if no ClientConnect is added', () => {
        const clientConnectCollection: IClientConnect[] = [sampleWithRequiredData];
        expectedResult = service.addClientConnectToCollectionIfMissing(clientConnectCollection, undefined, null);
        expect(expectedResult).toEqual(clientConnectCollection);
      });
    });

    describe('compareClientConnect', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareClientConnect(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareClientConnect(entity1, entity2);
        const compareResult2 = service.compareClientConnect(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareClientConnect(entity1, entity2);
        const compareResult2 = service.compareClientConnect(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareClientConnect(entity1, entity2);
        const compareResult2 = service.compareClientConnect(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
