import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CustomerFeatureShell } from './customer-feature-shell';

describe('CustomerFeatureShell', () => {
  let component: CustomerFeatureShell;
  let fixture: ComponentFixture<CustomerFeatureShell>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerFeatureShell],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomerFeatureShell);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
