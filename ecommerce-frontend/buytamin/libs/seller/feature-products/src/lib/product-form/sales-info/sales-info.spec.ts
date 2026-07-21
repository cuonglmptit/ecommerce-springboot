import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SalesInfo } from './sales-info';

describe('SalesInfo', () => {
  let component: SalesInfo;
  let fixture: ComponentFixture<SalesInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SalesInfo],
    }).compileComponents();

    fixture = TestBed.createComponent(SalesInfo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
