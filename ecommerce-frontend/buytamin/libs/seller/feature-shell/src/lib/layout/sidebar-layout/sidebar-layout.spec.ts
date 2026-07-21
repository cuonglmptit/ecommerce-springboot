import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SidebarLayout } from './sidebar-layout';

describe('SidebarLayout', () => {
  let component: SidebarLayout;
  let fixture: ComponentFixture<SidebarLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SidebarLayout],
    }).compileComponents();

    fixture = TestBed.createComponent(SidebarLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
