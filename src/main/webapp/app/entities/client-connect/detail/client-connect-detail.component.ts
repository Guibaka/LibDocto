import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IClientConnect } from '../client-connect.model';

@Component({
  selector: 'jhi-client-connect-detail',
  templateUrl: './client-connect-detail.component.html',
})
export class ClientConnectDetailComponent implements OnInit {
  clientConnect: IClientConnect | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clientConnect }) => {
      this.clientConnect = clientConnect;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
