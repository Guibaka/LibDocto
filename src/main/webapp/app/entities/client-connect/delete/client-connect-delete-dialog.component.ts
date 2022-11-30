import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IClientConnect } from '../client-connect.model';
import { ClientConnectService } from '../service/client-connect.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './client-connect-delete-dialog.component.html',
})
export class ClientConnectDeleteDialogComponent {
  clientConnect?: IClientConnect;

  constructor(protected clientConnectService: ClientConnectService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.clientConnectService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
