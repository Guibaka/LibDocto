import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DoctorFormService } from './doctor-form.service';
import { DoctorService } from '../service/doctor.service';
import { IDoctor } from '../doctor.model';
import { ICalendar } from 'app/entities/calendar/calendar.model';
import { CalendarService } from 'app/entities/calendar/service/calendar.service';

import { DoctorUpdateComponent } from './doctor-update.component';

describe('Doctor Management Update Component', () => {
  let comp: DoctorUpdateComponent;
  let fixture: ComponentFixture<DoctorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let doctorFormService: DoctorFormService;
  let doctorService: DoctorService;
  let calendarService: CalendarService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DoctorUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(DoctorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DoctorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    doctorFormService = TestBed.inject(DoctorFormService);
    doctorService = TestBed.inject(DoctorService);
    calendarService = TestBed.inject(CalendarService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Calendar query and add missing value', () => {
      const doctor: IDoctor = { id: 456 };
      const calendar: ICalendar = { id: 61622 };
      doctor.calendar = calendar;

      const calendarCollection: ICalendar[] = [{ id: 18914 }];
      jest.spyOn(calendarService, 'query').mockReturnValue(of(new HttpResponse({ body: calendarCollection })));
      const additionalCalendars = [calendar];
      const expectedCollection: ICalendar[] = [...additionalCalendars, ...calendarCollection];
      jest.spyOn(calendarService, 'addCalendarToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      expect(calendarService.query).toHaveBeenCalled();
      expect(calendarService.addCalendarToCollectionIfMissing).toHaveBeenCalledWith(
        calendarCollection,
        ...additionalCalendars.map(expect.objectContaining)
      );
      expect(comp.calendarsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const doctor: IDoctor = { id: 456 };
      const calendar: ICalendar = { id: 33066 };
      doctor.calendar = calendar;

      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      expect(comp.calendarsSharedCollection).toContain(calendar);
      expect(comp.doctor).toEqual(doctor);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 123 };
      jest.spyOn(doctorFormService, 'getDoctor').mockReturnValue(doctor);
      jest.spyOn(doctorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: doctor }));
      saveSubject.complete();

      // THEN
      expect(doctorFormService.getDoctor).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(doctorService.update).toHaveBeenCalledWith(expect.objectContaining(doctor));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 123 };
      jest.spyOn(doctorFormService, 'getDoctor').mockReturnValue({ id: null });
      jest.spyOn(doctorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: doctor }));
      saveSubject.complete();

      // THEN
      expect(doctorFormService.getDoctor).toHaveBeenCalled();
      expect(doctorService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDoctor>>();
      const doctor = { id: 123 };
      jest.spyOn(doctorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ doctor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(doctorService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCalendar', () => {
      it('Should forward to calendarService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(calendarService, 'compareCalendar');
        comp.compareCalendar(entity, entity2);
        expect(calendarService.compareCalendar).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
