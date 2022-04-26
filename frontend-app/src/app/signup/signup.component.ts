import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Credentials } from '../models/credentials.model';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  credentials: Credentials = new Credentials('', '', '');
  baseUrl: string = 'http://localhost:8080/api';

  constructor(private notificationService: NotificationService, private httpClient: HttpClient, private router: Router) { }

  ngOnInit(): void {
  }

  public onSave(form: NgForm) {

    this.httpClient
        .post<any>(`${this.baseUrl}/accounts/create`, {'username': form.value.username, 'password': form.value.password})
        .subscribe({
            next: data => {
                this.notificationService.pushNotification('Confirmation', 'Account created successfully, you can sign in now.');
                this.router.navigateByUrl('/signin');
            },
            error: error => {
              const errorMessage = error.error.message ? error.error.message : 'Something went wrong. Account not created.';
              this.notificationService.pushNotification('Error', errorMessage);
            }
        });
  }
}
