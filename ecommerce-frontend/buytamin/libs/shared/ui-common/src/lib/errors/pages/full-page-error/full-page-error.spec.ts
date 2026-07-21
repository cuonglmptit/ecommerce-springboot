import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FullPageError } from './full-page-error';

describe('FullPageError', () => {
  let component: FullPageError;
  let fixture: ComponentFixture<FullPageError>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FullPageError],
    }).compileComponents();

    fixture = TestBed.createComponent(FullPageError);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
