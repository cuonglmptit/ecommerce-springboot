import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BasicInfo } from './basic-info';

describe('BasicInfo', () => {
  let component: BasicInfo;
  let fixture: ComponentFixture<BasicInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BasicInfo],
    }).compileComponents();

    fixture = TestBed.createComponent(BasicInfo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
