import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SellerFeatureShell } from './seller-feature-shell';

describe('SellerFeatureShell', () => {
  let component: SellerFeatureShell;
  let fixture: ComponentFixture<SellerFeatureShell>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SellerFeatureShell],
    }).compileComponents();

    fixture = TestBed.createComponent(SellerFeatureShell);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
