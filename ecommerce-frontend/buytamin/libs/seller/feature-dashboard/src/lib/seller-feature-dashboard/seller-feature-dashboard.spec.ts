import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SellerFeatureDashboard } from './seller-feature-dashboard';

describe('SellerFeatureDashboard', () => {
  let component: SellerFeatureDashboard;
  let fixture: ComponentFixture<SellerFeatureDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SellerFeatureDashboard],
    }).compileComponents();

    fixture = TestBed.createComponent(SellerFeatureDashboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
