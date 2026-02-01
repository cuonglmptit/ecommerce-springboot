import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SellerFeatureShop } from './seller-feature-shop';

describe('SellerFeatureShop', () => {
  let component: SellerFeatureShop;
  let fixture: ComponentFixture<SellerFeatureShop>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SellerFeatureShop],
    }).compileComponents();

    fixture = TestBed.createComponent(SellerFeatureShop);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
