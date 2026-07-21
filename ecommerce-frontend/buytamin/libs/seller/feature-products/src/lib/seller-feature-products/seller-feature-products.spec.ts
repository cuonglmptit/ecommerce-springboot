import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SellerFeatureProducts } from './seller-feature-products';

describe('SellerFeatureProducts', () => {
  let component: SellerFeatureProducts;
  let fixture: ComponentFixture<SellerFeatureProducts>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SellerFeatureProducts],
    }).compileComponents();

    fixture = TestBed.createComponent(SellerFeatureProducts);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
