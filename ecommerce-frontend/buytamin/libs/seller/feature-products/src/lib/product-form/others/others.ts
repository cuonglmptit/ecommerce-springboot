import { Component } from '@angular/core';
import {
  ControlContainer,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';

@Component({
  selector: 'seller-others',
  imports: [ReactiveFormsModule],
  templateUrl: './others.html',
  styleUrl: './others.scss',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class Others {}
