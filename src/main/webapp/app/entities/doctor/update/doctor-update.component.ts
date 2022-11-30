import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DoctorFormService, DoctorFormGroup } from './doctor-form.service';
import { IDoctor } from '../doctor.model';
import { DoctorService } from '../service/doctor.service';
import { ICalendar } from 'app/entities/calendar/calendar.model';
import { CalendarService } from 'app/entities/calendar/service/calendar.service';

@Component({
  selector: 'jhi-doctor-update',
  templateUrl: './doctor-update.component.html',
})
export class DoctorUpdateComponent implements OnInit {
  isSaving = false;
  doctor: IDoctor | null = null;

  calendarsSharedCollection: ICalendar[] = [];

  editForm: DoctorFormGroup = this.doctorFormService.createDoctorFormGroup();

  constructor(
    protected doctorService: DoctorService,
    protected doctorFormService: DoctorFormService,
    protected calendarService: CalendarService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCalendar = (o1: ICalendar | null, o2: ICalendar | null): boolean => this.calendarService.compareCalendar(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ doctor }) => {
      this.doctor = doctor;
      if (doctor) {
        this.updateForm(doctor);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const doctor = this.doctorFormService.getDoctor(this.editForm);
    if (doctor.id !== null) {
      this.subscribeToSaveResponse(this.doctorService.update(doctor));
    } else {
      this.subscribeToSaveResponse(this.doctorService.create(doctor));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDoctor>>): void {
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

  protected updateForm(doctor: IDoctor): void {
    this.doctor = doctor;
    this.doctorFormService.resetForm(this.editForm, doctor);

    this.calendarsSharedCollection = this.calendarService.addCalendarToCollectionIfMissing<ICalendar>(
      this.calendarsSharedCollection,
      doctor.calendar
    );
  }

  protected loadRelationshipsOptions(): void {
    this.calendarService
      .query()
      .pipe(map((res: HttpResponse<ICalendar[]>) => res.body ?? []))
      .pipe(
        map((calendars: ICalendar[]) => this.calendarService.addCalendarToCollectionIfMissing<ICalendar>(calendars, this.doctor?.calendar))
      )
      .subscribe((calendars: ICalendar[]) => (this.calendarsSharedCollection = calendars));
  }
}
