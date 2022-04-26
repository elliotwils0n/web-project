import { Component, OnInit } from '@angular/core';
import { ApiCallerService } from '../services/api-caller.service';
import { AuthorizationSerice } from '../services/authorization.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit {

  constructor(private apiCallerService: ApiCallerService, private authorizationService: AuthorizationSerice, private notificationService: NotificationService) { }

  ngOnInit(): void {
  }

  public deleteAccount() {
    this.apiCallerService.delete(`/accounts/delete`).subscribe({
            next: data => {
                this.notificationService.pushNotification('Confirmation', 'Account deleted successfully.');
            },
            error: error => {
              const errorMessage = error.error.message ? error.error.message : 'Something went wrong.';
              this.notificationService.pushNotification('Error', errorMessage);
            }
        });
        
    this.authorizationService.logOut();
  }

}
