import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DoctorService } from '../service/doctor.service';

import { DoctorComponent } from './doctor.component';

describe('Doctor Management Component', () => {
  let comp: DoctorComponent;
  let fixture: ComponentFixture<DoctorComponent>;
  let service: DoctorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'doctor', component: DoctorComponent }]), HttpClientTestingModule],
      declarations: [DoctorComponent],
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
      .overrideTemplate(DoctorComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DoctorComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DoctorService);

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
    expect(comp.doctors?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to doctorService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getDoctorIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getDoctorIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
