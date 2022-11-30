import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { SlotFormService, SlotFormGroup } from './slot-form.service';
import { ISlot } from '../slot.model';
import { SlotService } from '../service/slot.service';
import { IDoctor } from 'app/entities/doctor/doctor.model';
import { DoctorService } from 'app/entities/doctor/service/doctor.service';
import { IClientConnect } from 'app/entities/client-connect/client-connect.model';
import { ClientConnectService } from 'app/entities/client-connect/service/client-connect.service';
import { ICalendar } from 'app/entities/calendar/calendar.model';
import { CalendarService } from 'app/entities/calendar/service/calendar.service';
import { StateSlot } from 'app/entities/enumerations/state-slot.model';

@Component({
  selector: 'jhi-slot-update',
  templateUrl: './slot-update.component.html',
})
export class SlotUpdateComponent implements OnInit {
  isSaving = false;
  slot: ISlot | null = null;
  stateSlotValues = Object.keys(StateSlot);

  doctorsSharedCollection: IDoctor[] = [];
  clientConnectsSharedCollection: IClientConnect[] = [];
  calendarsSharedCollection: ICalendar[] = [];

  editForm: SlotFormGroup = this.slotFormService.createSlotFormGroup();

  constructor(
    protected slotService: SlotService,
    protected slotFormService: SlotFormService,
    protected doctorService: DoctorService,
    protected clientConnectService: ClientConnectService,
    protected calendarService: CalendarService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDoctor = (o1: IDoctor | null, o2: IDoctor | null): boolean => this.doctorService.compareDoctor(o1, o2);

  compareClientConnect = (o1: IClientConnect | null, o2: IClientConnect | null): boolean =>
    this.clientConnectService.compareClientConnect(o1, o2);

  compareCalendar = (o1: ICalendar | null, o2: ICalendar | null): boolean => this.calendarService.compareCalendar(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ slot }) => {
      this.slot = slot;
      if (slot) {
        this.updateForm(slot);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const slot = this.slotFormService.getSlot(this.editForm);
    if (slot.id !== null) {
      this.subscribeToSaveResponse(this.slotService.update(slot));
    } else {
      this.subscribeToSaveResponse(this.slotService.create(slot));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISlot>>): void {
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

  protected updateForm(slot: ISlot): void {
    this.slot = slot;
    this.slotFormService.resetForm(this.editForm, slot);

    this.doctorsSharedCollection = this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(this.doctorsSharedCollection, slot.doctor);
    this.clientConnectsSharedCollection = this.clientConnectService.addClientConnectToCollectionIfMissing<IClientConnect>(
      this.clientConnectsSharedCollection,
      slot.clientConnect
    );
    this.calendarsSharedCollection = this.calendarService.addCalendarToCollectionIfMissing<ICalendar>(
      this.calendarsSharedCollection,
      slot.calendar
    );
  }

  protected loadRelationshipsOptions(): void {
    this.doctorService
      .query()
      .pipe(map((res: HttpResponse<IDoctor[]>) => res.body ?? []))
      .pipe(map((doctors: IDoctor[]) => this.doctorService.addDoctorToCollectionIfMissing<IDoctor>(doctors, this.slot?.doctor)))
      .subscribe((doctors: IDoctor[]) => (this.doctorsSharedCollection = doctors));

    this.clientConnectService
      .query()
      .pipe(map((res: HttpResponse<IClientConnect[]>) => res.body ?? []))
      .pipe(
        map((clientConnects: IClientConnect[]) =>
          this.clientConnectService.addClientConnectToCollectionIfMissing<IClientConnect>(clientConnects, this.slot?.clientConnect)
        )
      )
      .subscribe((clientConnects: IClientConnect[]) => (this.clientConnectsSharedCollection = clientConnects));

    this.calendarService
      .query()
      .pipe(map((res: HttpResponse<ICalendar[]>) => res.body ?? []))
      .pipe(
        map((calendars: ICalendar[]) => this.calendarService.addCalendarToCollectionIfMissing<ICalendar>(calendars, this.slot?.calendar))
      )
      .subscribe((calendars: ICalendar[]) => (this.calendarsSharedCollection = calendars));
  }
}
