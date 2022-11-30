import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { CalendarFormService, CalendarFormGroup } from './calendar-form.service';
import { ICalendar } from '../calendar.model';
import { CalendarService } from '../service/calendar.service';

@Component({
  selector: 'jhi-calendar-update',
  templateUrl: './calendar-update.component.html',
})
export class CalendarUpdateComponent implements OnInit {
  isSaving = false;
  calendar: ICalendar | null = null;

  editForm: CalendarFormGroup = this.calendarFormService.createCalendarFormGroup();

  constructor(
    protected calendarService: CalendarService,
    protected calendarFormService: CalendarFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ calendar }) => {
      this.calendar = calendar;
      if (calendar) {
        this.updateForm(calendar);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const calendar = this.calendarFormService.getCalendar(this.editForm);
    if (calendar.id !== null) {
      this.subscribeToSaveResponse(this.calendarService.update(calendar));
    } else {
      this.subscribeToSaveResponse(this.calendarService.create(calendar));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICalendar>>): void {
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

  protected updateForm(calendar: ICalendar): void {
    this.calendar = calendar;
    this.calendarFormService.resetForm(this.editForm, calendar);
  }
}
