import { Component } from '@angular/core';
import { Header } from '../layout/header/header';
import { Footer } from '../layout/footer/footer';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'seller-seller-feature-shell',
  imports: [Header, Footer, RouterOutlet],
  templateUrl: './seller-feature-shell.html',
  styleUrl: './seller-feature-shell.scss',
  standalone: true,
})
export class SellerFeatureShell {}
