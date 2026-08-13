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
})
export class Others {}
