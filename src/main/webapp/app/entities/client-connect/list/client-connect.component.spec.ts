import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ClientConnectService } from '../service/client-connect.service';

import { ClientConnectComponent } from './client-connect.component';

describe('ClientConnect Management Component', () => {
  let comp: ClientConnectComponent;
  let fixture: ComponentFixture<ClientConnectComponent>;
  let service: ClientConnectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'client-connect', component: ClientConnectComponent }]), HttpClientTestingModule],
      declarations: [ClientConnectComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ClientConnectComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClientConnectComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ClientConnectService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.clientConnects?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to clientConnectService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getClientConnectIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getClientConnectIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
