import { Appointments } from './appointments';
import { of } from 'rxjs';

describe('Appointments (Pure TS)', () => {
  let component: Appointments;
  let apiServiceMock: any;
  let fbMock: any;

  beforeEach(() => {
    apiServiceMock = {
      getPatients: jest.fn().mockReturnValue(of([])),
      getAppointments: jest.fn().mockReturnValue(of([])),
      bookAppointment: jest.fn().mockReturnValue(of({}))
    };
    fbMock = {
      group: jest.fn().mockReturnValue({
        valid: true,
        value: {},
        reset: jest.fn()
      })
    };
    component = new Appointments(apiServiceMock, fbMock);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load initial data on ngOnInit', () => {
    component.ngOnInit();
    expect(apiServiceMock.getPatients).toHaveBeenCalled();
    expect(apiServiceMock.getAppointments).toHaveBeenCalled();
  });
});
