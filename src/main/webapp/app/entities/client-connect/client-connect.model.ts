export interface IClientConnect {
  id: number;
  idClientConnect?: number | null;
  firstName?: string | null;
  lastName?: string | null;
  mail?: string | null;
  password?: string | null;
}

export type NewClientConnect = Omit<IClientConnect, 'id'> & { id: null };
