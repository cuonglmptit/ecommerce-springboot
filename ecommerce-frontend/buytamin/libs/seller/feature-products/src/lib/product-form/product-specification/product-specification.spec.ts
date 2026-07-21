import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductSpecification } from './product-specification';

describe('ProductSpecification', () => {
  let component: ProductSpecification;
  let fixture: ComponentFixture<ProductSpecification>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductSpecification],
    }).compileComponents();

    fixture = TestBed.createComponent(ProductSpecification);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
