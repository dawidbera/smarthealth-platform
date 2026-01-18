import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService, Patient } from './api';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService]
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch patients from API', () => {
    const mockPatients: Patient[] = [
      { id: 1, firstName: 'John', lastName: 'Doe', email: 'john@example.com', nationalId: '123' }
    ];

    service.getPatients().subscribe(patients => {
      expect(patients.length).toBe(1);
      expect(patients).toEqual(mockPatients);
    });

    const req = httpMock.expectOne('http://localhost:8080/patient');
    expect(req.request.method).toBe('GET');
    req.flush(mockPatients);
  });

  it('should create a new patient', () => {
    const newPatient: Patient = { firstName: 'Alice', lastName: 'Smith', email: 'alice@example.com', nationalId: '456' };

    service.createPatient(newPatient).subscribe(patient => {
      expect(patient.firstName).toBe('Alice');
    });

    const req = httpMock.expectOne('http://localhost:8080/patient');
    expect(req.request.method).toBe('POST');
    req.flush({ ...newPatient, id: 2 });
  });
});
