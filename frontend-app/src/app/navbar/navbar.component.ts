import { Component, OnInit } from '@angular/core';
import { AuthorizationSerice } from '../services/authorization.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  constructor(private authorizationSerivce: AuthorizationSerice) { }

  ngOnInit(): void {
  }

  public onLogout() {
    this.authorizationSerivce.logOut();
  }

  public showLogout() {
    return this.authorizationSerivce.isSessionActive();
  }

}
