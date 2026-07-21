import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from '../header/header';
import { Footer } from '../footer/footer';
import { Sidebar } from '../sidebar/sidebar';

@Component({
  selector: 'seller-sidebar-layout',
  imports: [RouterOutlet, Header, Footer, Sidebar],
  templateUrl: './sidebar-layout.html',
  styleUrl: './sidebar-layout.scss',
})
export class SidebarLayout {}
