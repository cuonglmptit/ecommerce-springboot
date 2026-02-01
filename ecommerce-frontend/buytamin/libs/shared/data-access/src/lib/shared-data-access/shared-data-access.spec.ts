import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SharedDataAccess } from './shared-data-access';

describe('SharedDataAccess', () => {
  let component: SharedDataAccess;
  let fixture: ComponentFixture<SharedDataAccess>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedDataAccess],
    }).compileComponents();

    fixture = TestBed.createComponent(SharedDataAccess);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
