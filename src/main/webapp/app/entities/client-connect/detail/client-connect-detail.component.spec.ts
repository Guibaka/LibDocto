import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ClientConnectDetailComponent } from './client-connect-detail.component';

describe('ClientConnect Management Detail Component', () => {
  let comp: ClientConnectDetailComponent;
  let fixture: ComponentFixture<ClientConnectDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ClientConnectDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ clientConnect: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ClientConnectDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ClientConnectDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load clientConnect on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.clientConnect).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
