import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ClientConnectFormService, ClientConnectFormGroup } from './client-connect-form.service';
import { IClientConnect } from '../client-connect.model';
import { ClientConnectService } from '../service/client-connect.service';

@Component({
  selector: 'jhi-client-connect-update',
  templateUrl: './client-connect-update.component.html',
})
export class ClientConnectUpdateComponent implements OnInit {
  isSaving = false;
  clientConnect: IClientConnect | null = null;

  editForm: ClientConnectFormGroup = this.clientConnectFormService.createClientConnectFormGroup();

  constructor(
    protected clientConnectService: ClientConnectService,
    protected clientConnectFormService: ClientConnectFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clientConnect }) => {
      this.clientConnect = clientConnect;
      if (clientConnect) {
        this.updateForm(clientConnect);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const clientConnect = this.clientConnectFormService.getClientConnect(this.editForm);
    if (clientConnect.id !== null) {
      this.subscribeToSaveResponse(this.clientConnectService.update(clientConnect));
    } else {
      this.subscribeToSaveResponse(this.clientConnectService.create(clientConnect));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClientConnect>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(clientConnect: IClientConnect): void {
    this.clientConnect = clientConnect;
    this.clientConnectFormService.resetForm(this.editForm, clientConnect);
  }
}
