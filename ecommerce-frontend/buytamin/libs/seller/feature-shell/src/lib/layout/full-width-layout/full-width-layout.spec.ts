import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FullWidthLayout } from './full-width-layout';

describe('FullWidthLayout', () => {
  let component: FullWidthLayout;
  let fixture: ComponentFixture<FullWidthLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FullWidthLayout],
    }).compileComponents();

    fixture = TestBed.createComponent(FullWidthLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
