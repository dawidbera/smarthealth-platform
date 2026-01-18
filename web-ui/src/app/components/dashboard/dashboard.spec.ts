import { Dashboard } from './dashboard';
import { of } from 'rxjs';

describe('Dashboard (Pure TS)', () => {
  let component: Dashboard;
  let apiServiceMock: any;

  beforeEach(() => {
    apiServiceMock = {
      getLatestTelemetry: jest.fn().mockReturnValue(of({})),
      getTelemetryHistory: jest.fn().mockReturnValue(of([])),
      getTelemetryStats: jest.fn().mockReturnValue(of({}))
    };
    component = new Dashboard(apiServiceMock);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load telemetry data on ngOnInit', () => {
    component.ngOnInit();
    expect(apiServiceMock.getLatestTelemetry).toHaveBeenCalledWith('HR-MON-001');
    expect(apiServiceMock.getTelemetryHistory).toHaveBeenCalled();
    
    // Clean up interval subscriptions
    component.ngOnDestroy();
  });
});