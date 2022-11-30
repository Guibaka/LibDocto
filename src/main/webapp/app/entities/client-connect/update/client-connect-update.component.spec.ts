import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ClientConnectFormService } from './client-connect-form.service';
import { ClientConnectService } from '../service/client-connect.service';
import { IClientConnect } from '../client-connect.model';

import { ClientConnectUpdateComponent } from './client-connect-update.component';

describe('ClientConnect Management Update Component', () => {
  let comp: ClientConnectUpdateComponent;
  let fixture: ComponentFixture<ClientConnectUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let clientConnectFormService: ClientConnectFormService;
  let clientConnectService: ClientConnectService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ClientConnectUpdateComponent],
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
      .overrideTemplate(ClientConnectUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClientConnectUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    clientConnectFormService = TestBed.inject(ClientConnectFormService);
    clientConnectService = TestBed.inject(ClientConnectService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const clientConnect: IClientConnect = { id: 456 };

      activatedRoute.data = of({ clientConnect });
      comp.ngOnInit();

      expect(comp.clientConnect).toEqual(clientConnect);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientConnect>>();
      const clientConnect = { id: 123 };
      jest.spyOn(clientConnectFormService, 'getClientConnect').mockReturnValue(clientConnect);
      jest.spyOn(clientConnectService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientConnect });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientConnect }));
      saveSubject.complete();

      // THEN
      expect(clientConnectFormService.getClientConnect).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(clientConnectService.update).toHaveBeenCalledWith(expect.objectContaining(clientConnect));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientConnect>>();
      const clientConnect = { id: 123 };
      jest.spyOn(clientConnectFormService, 'getClientConnect').mockReturnValue({ id: null });
      jest.spyOn(clientConnectService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientConnect: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientConnect }));
      saveSubject.complete();

      // THEN
      expect(clientConnectFormService.getClientConnect).toHaveBeenCalled();
      expect(clientConnectService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientConnect>>();
      const clientConnect = { id: 123 };
      jest.spyOn(clientConnectService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientConnect });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(clientConnectService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
