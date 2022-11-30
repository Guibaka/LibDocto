import { IClientConnect, NewClientConnect } from './client-connect.model';

export const sampleWithRequiredData: IClientConnect = {
  id: 47590,
  idClientConnect: 7926,
};

export const sampleWithPartialData: IClientConnect = {
  id: 81054,
  idClientConnect: 23978,
};

export const sampleWithFullData: IClientConnect = {
  id: 29148,
  idClientConnect: 29435,
  firstName: 'Zoie',
  lastName: 'Morar',
  mail: 'Franc Brand Tasty',
  password: 'functionalities',
};

export const sampleWithNewData: NewClientConnect = {
  idClientConnect: 29186,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
