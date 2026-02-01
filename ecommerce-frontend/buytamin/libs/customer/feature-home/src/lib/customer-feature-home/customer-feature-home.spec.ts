import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CustomerFeatureHome } from './customer-feature-home';

describe('CustomerFeatureHome', () => {
  let component: CustomerFeatureHome;
  let fixture: ComponentFixture<CustomerFeatureHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerFeatureHome],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomerFeatureHome);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
