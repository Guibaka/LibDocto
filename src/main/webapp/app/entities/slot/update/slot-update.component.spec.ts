import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SlotFormService } from './slot-form.service';
import { SlotService } from '../service/slot.service';
import { ISlot } from '../slot.model';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';
import { IClientConnect } from 'app/entities/client-connect/client-connect.model';
import { ClientConnectService } from 'app/entities/client-connect/service/client-connect.service';
import { ICalendar } from 'app/entities/calendar/calendar.model';
import { CalendarService } from 'app/entities/calendar/service/calendar.service';

import { SlotUpdateComponent } from './slot-update.component';

describe('Slot Management Update Component', () => {
  let comp: SlotUpdateComponent;
  let fixture: ComponentFixture<SlotUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let slotFormService: SlotFormService;
  let slotService: SlotService;
  let doctorService: DoctorService;
  let clientConnectService: ClientConnectService;
  let calendarService: CalendarService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SlotUpdateComponent],
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
      .overrideTemplate(SlotUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SlotUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    slotFormService = TestBed.inject(SlotFormService);
    slotService = TestBed.inject(SlotService);
    doctorService = TestBed.inject(DoctorService);
    clientConnectService = TestBed.inject(ClientConnectService);
    calendarService = TestBed.inject(CalendarService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Doctor query and add missing value', () => {
      const slot: ISlot = { id: 456 };
      const doctor: IDoctor = { id: 96361 };
      slot.doctor = doctor;

      const doctorCollection: IDoctor[] = [{ id: 14053 }];
      jest.spyOn(doctorService, 'query').mockReturnValue(of(new HttpResponse({ body: doctorCollection })));
      const additionalDoctors = [doctor];
      const expectedCollection: IDoctor[] = [...additionalDoctors, ...doctorCollection];
      jest.spyOn(doctorService, 'addDoctorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ slot });
      comp.ngOnInit();

      expect(doctorService.query).toHaveBeenCalled();
      expect(doctorService.addDoctorToCollectionIfMissing).toHaveBeenCalledWith(
        doctorCollection,
        ...additionalDoctors.map(expect.objectContaining)
      );
      expect(comp.doctorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ClientConnect query and add missing value', () => {
      const slot: ISlot = { id: 456 };
      const clientConnect: IClientConnect = { id: 68137 };
      slot.clientConnect = clientConnect;

      const clientConnectCollection: IClientConnect[] = [{ id: 73255 }];
      jest.spyOn(clientConnectService, 'query').mockReturnValue(of(new HttpResponse({ body: clientConnectCollection })));
      const additionalClientConnects = [clientConnect];
      const expectedCollection: IClientConnect[] = [...additionalClientConnects, ...clientConnectCollection];
      jest.spyOn(clientConnectService, 'addClientConnectToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ slot });
      comp.ngOnInit();

      expect(clientConnectService.query).toHaveBeenCalled();
      expect(clientConnectService.addClientConnectToCollectionIfMissing).toHaveBeenCalledWith(
        clientConnectCollection,
        ...additionalClientConnects.map(expect.objectContaining)
      );
      expect(comp.clientConnectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Calendar query and add missing value', () => {
      const slot: ISlot = { id: 456 };
      const calendar: ICalendar = { id: 5017 };
      slot.calendar = calendar;

      const calendarCollection: ICalendar[] = [{ id: 98535 }];
      jest.spyOn(calendarService, 'query').mockReturnValue(of(new HttpResponse({ body: calendarCollection })));
      const additionalCalendars = [calendar];
      const expectedCollection: ICalendar[] = [...additionalCalendars, ...calendarCollection];
      jest.spyOn(calendarService, 'addCalendarToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ slot });
      comp.ngOnInit();

      expect(calendarService.query).toHaveBeenCalled();
      expect(calendarService.addCalendarToCollectionIfMissing).toHaveBeenCalledWith(
        calendarCollection,
        ...additionalCalendars.map(expect.objectContaining)
      );
      expect(comp.calendarsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const slot: ISlot = { id: 456 };
      const doctor: IDoctor = { id: 15571 };
      slot.doctor = doctor;
      const clientConnect: IClientConnect = { id: 87369 };
      slot.clientConnect = clientConnect;
      const calendar: ICalendar = { id: 36094 };
      slot.calendar = calendar;

      activatedRoute.data = of({ slot });
      comp.ngOnInit();

      expect(comp.doctorsSharedCollection).toContain(doctor);
      expect(comp.clientConnectsSharedCollection).toContain(clientConnect);
      expect(comp.calendarsSharedCollection).toContain(calendar);
      expect(comp.slot).toEqual(slot);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISlot>>();
      const slot = { id: 123 };
      jest.spyOn(slotFormService, 'getSlot').mockReturnValue(slot);
      jest.spyOn(slotService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ slot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: slot }));
      saveSubject.complete();

      // THEN
      expect(slotFormService.getSlot).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(slotService.update).toHaveBeenCalledWith(expect.objectContaining(slot));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISlot>>();
      const slot = { id: 123 };
      jest.spyOn(slotFormService, 'getSlot').mockReturnValue({ id: null });
      jest.spyOn(slotService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ slot: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: slot }));
      saveSubject.complete();

      // THEN
      expect(slotFormService.getSlot).toHaveBeenCalled();
      expect(slotService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISlot>>();
      const slot = { id: 123 };
      jest.spyOn(slotService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ slot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(slotService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDoctor', () => {
      it('Should forward to doctorService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(doctorService, 'compareDoctor');
        comp.compareDoctor(entity, entity2);
        expect(doctorService.compareDoctor).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareClientConnect', () => {
      it('Should forward to clientConnectService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(clientConnectService, 'compareClientConnect');
        comp.compareClientConnect(entity, entity2);
        expect(clientConnectService.compareClientConnect).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
