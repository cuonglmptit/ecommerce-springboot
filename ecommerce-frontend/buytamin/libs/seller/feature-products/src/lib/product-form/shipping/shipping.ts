import { Component } from '@angular/core';
import {
  ControlContainer,
  FormGroupDirective,
  ReactiveFormsModule,
} from '@angular/forms';

@Component({
  selector: 'seller-shipping',
  imports: [ReactiveFormsModule],
  templateUrl: './shipping.html',
  styleUrl: './shipping.scss',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class Shipping {}
