import { of } from 'rxjs';
import { ApiService } from './api';

describe('ApiService (Pure TS)', () => {
  let service: ApiService;
  let httpClientMock: any;

  beforeEach(() => {
    httpClientMock = {
      get: jest.fn(),
      post: jest.fn()
    };
    service = new ApiService(httpClientMock);
  });

  it('should fetch patients using HttpClient', () => {
    const mockPatients = [{ firstName: 'John' }];
    httpClientMock.get.mockReturnValue(of(mockPatients));

    service.getPatients().subscribe(data => {
      expect(data).toEqual(mockPatients);
    });

    expect(httpClientMock.get).toHaveBeenCalledWith('http://localhost:8080/patient');
  });
});
