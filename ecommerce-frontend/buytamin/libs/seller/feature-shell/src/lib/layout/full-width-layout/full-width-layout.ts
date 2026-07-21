import { Component } from '@angular/core';
import { Footer } from '../footer/footer';
import { Header } from '../header/header';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'seller-full-width-layout',
  imports: [Footer, Header, RouterOutlet],
  templateUrl: './full-width-layout.html',
  styleUrl: './full-width-layout.scss',
})
export class FullWidthLayout {}
