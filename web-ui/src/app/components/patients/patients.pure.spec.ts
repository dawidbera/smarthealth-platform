import { Patients } from './patients';
import { of } from 'rxjs';

describe('Patients (Pure TS)', () => {
  let component: Patients;
  let apiServiceMock: any;
  let fbMock: any;

  beforeEach(() => {
    apiServiceMock = {
      getPatients: jest.fn().mockReturnValue(of([])),
      createPatient: jest.fn()
    };
    fbMock = {
      group: jest.fn().mockReturnValue({
        valid: true,
        value: {},
        reset: jest.fn()
      })
    };
    component = new Patients(apiServiceMock, fbMock);
  });

  it('should load patients on init', () => {
    const mockPatients = [{ firstName: 'John', lastName: 'Doe' }];
    apiServiceMock.getPatients.mockReturnValue(of(mockPatients));

    component.ngOnInit();

    expect(component.patients).toEqual(mockPatients);
    expect(apiServiceMock.getPatients).toHaveBeenCalled();
  });
});
