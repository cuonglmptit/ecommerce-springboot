import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminFeatureShell } from './admin-feature-shell';

describe('AdminFeatureShell', () => {
  let component: AdminFeatureShell;
  let fixture: ComponentFixture<AdminFeatureShell>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminFeatureShell],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminFeatureShell);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
