import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminFeatureManagement } from './admin-feature-management';

describe('AdminFeatureManagement', () => {
  let component: AdminFeatureManagement;
  let fixture: ComponentFixture<AdminFeatureManagement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminFeatureManagement],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminFeatureManagement);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
