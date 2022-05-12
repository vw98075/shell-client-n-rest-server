export interface IDirectory {
  id?: number;
  path?: string;
  createdTime?: Date | null;
}

export class Directory implements IDirectory {
  constructor(public id?: number, public path?: string, public createdTime?: Date | null) {}
}
