import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SlotService } from '../service/slot.service';

import { SlotComponent } from './slot.component';

describe('Slot Management Component', () => {
  let comp: SlotComponent;
  let fixture: ComponentFixture<SlotComponent>;
  let service: SlotService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'slot', component: SlotComponent }]), HttpClientTestingModule],
      declarations: [SlotComponent],
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
      .overrideTemplate(SlotComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SlotComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SlotService);

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
    expect(comp.slots?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to slotService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSlotIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSlotIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
