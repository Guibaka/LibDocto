import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SlotDetailComponent } from './slot-detail.component';

describe('Slot Management Detail Component', () => {
  let comp: SlotDetailComponent;
  let fixture: ComponentFixture<SlotDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SlotDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ slot: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SlotDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SlotDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load slot on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.slot).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
