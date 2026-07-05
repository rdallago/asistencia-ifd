import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MarcadorAsistencia } from './marcador-asistencia';

describe('MarcadorAsistencia', () => {
  let component: MarcadorAsistencia;
  let fixture: ComponentFixture<MarcadorAsistencia>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MarcadorAsistencia],
    }).compileComponents();

    fixture = TestBed.createComponent(MarcadorAsistencia);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
