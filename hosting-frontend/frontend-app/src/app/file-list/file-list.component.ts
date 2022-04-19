import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { File } from '../models/file.model';
import { AuthorizationSerice } from '../services/authorization.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-file-list',
  templateUrl: './file-list.component.html',
  styleUrls: ['./file-list.component.css']
})
export class FileListComponent implements OnInit {

  baseUrl: string = 'http://localhost:8080/api';
  files: File[] = []

  constructor(private httpClient: HttpClient, private authorizationService: AuthorizationSerice, private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.refreshFileList();
  }

  public refreshFileList() {
    const headers = {'Authorization': this.authorizationService.getAccessToken()};
    this.httpClient.get<any>(`${this.baseUrl}/files/list`, {headers: headers}).subscribe({
      next: data => {
        this.files = data;
      },
      error: error => {
        let errorMessage = 'Something went wrong.';
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

  public downloadFile(id: number) {
    const headers = {'Authorization': this.authorizationService.getAccessToken()};
    this.httpClient.get<any>(`${this.baseUrl}/files/download/${id}`, {headers: headers}).subscribe({
      next: data => {
        console.log('File downloaded successfully.');
      },
      error: error => {
        let errorMessage = 'Something went wrong.';
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

  public deleteFile(id: number) {
    const headers = {'Authorization': this.authorizationService.getAccessToken()};
    this.httpClient.delete<any>(`${this.baseUrl}/files/delete/${id}`, {headers: headers}).subscribe({
      next: data => {
        this.refreshFileList();
      },
      error: error => {
        let errorMessage = 'Something went wrong.';
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
