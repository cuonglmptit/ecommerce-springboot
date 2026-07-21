import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BaseProductModels } from './base-product.model';

describe('BaseProductModels', () => {
  let component: BaseProductModels;
  let fixture: ComponentFixture<BaseProductModels>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BaseProductModels],
    }).compileComponents();

    fixture = TestBed.createComponent(BaseProductModels);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
