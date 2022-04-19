import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthorizationSerice } from '../services/authorization.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit {

  baseUrl: string = 'http://localhost:8080/api';

  constructor(private notificationService: NotificationService, private authorizationService: AuthorizationSerice, private httpClient: HttpClient, private router: Router) { }

  ngOnInit(): void {
  }

  public deleteAccount() {
    const headers = {'Authorization': this.authorizationService.getAccessToken()};
    this.httpClient
        .delete<any>(`${this.baseUrl}/accounts/delete`, {headers: headers})
        .subscribe({
            next: data => {
                this.notificationService.pushNotification('Confirmation', 'Account deleted successfully.');
                this.authorizationService.clearStorage();
                this.router.navigateByUrl('/signin');
            },
            error: error => {
              let errorMessage = 'Something went wrong. Account not deleted.';
              if(error.status == 403) {
                this.authorizationService.clearStorage();
                errorMessage = 'Session expired.';
              } else {
                errorMessage = error.error.message;
              }
              this.notificationService.pushNotification('Error', errorMessage);
            }
        });
  }

}
