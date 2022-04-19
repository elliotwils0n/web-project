import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Credentials } from '../models/credentials.model';
import { AuthorizationSerice } from '../services/authorization.service';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SigninComponent implements OnInit {

  credentials: Credentials = new Credentials('', '', '');

  constructor(private router: Router, private authorizationSerivce: AuthorizationSerice) { }

  ngOnInit(): void {
    
  }

  public onSave(form: NgForm) {
    this.credentials.username = form.value.username;
    this.credentials.password = form.value.password;

    this.authorizationSerivce.generateTokens(this.credentials);
  }

}
