import { Billing } from './billing';
import { of } from 'rxjs';

describe('Billing (Pure TS)', () => {
  let component: Billing;
  let apiServiceMock: any;

  beforeEach(() => {
    apiServiceMock = {
      getInvoices: jest.fn().mockReturnValue(of([]))
    };
    component = new Billing(apiServiceMock);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load invoices on init', () => {
    const mockInvoices = [{ id: 1, amount: 100 }];
    apiServiceMock.getInvoices.mockReturnValue(of(mockInvoices));

    component.ngOnInit();

    expect(component.invoices).toEqual(mockInvoices);
  });
});