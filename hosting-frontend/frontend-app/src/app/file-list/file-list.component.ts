import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { FileInfo } from '../models/file-info.model';
import { AuthorizationSerice } from '../services/authorization.service';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-file-list',
  templateUrl: './file-list.component.html',
  styleUrls: ['./file-list.component.css']
})
export class FileListComponent implements OnInit {

  baseUrl: string = 'http://localhost:8080/api';
  files: FileInfo[] = []
  file: File | null = null;

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
        this.notificationService.pushNotification('Success', 'File deleted successfully');
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

  public uploadFile() {
    if(this.file) {
        const headers = {'Authorization': this.authorizationService.getAccessToken()};

        let formData = new FormData();
        formData.append('file', this.file);

        this.httpClient.post<any>(`${this.baseUrl}/files/upload`, formData, {headers: headers}).subscribe({
          next: data => {
            this.refreshFileList();
            this.notificationService.pushNotification('Success', 'File uploaded Successfully.');
          },
          error: error => {
            let errorMessage = 'Something went wrong while uploading a file.';
            if(error.status == 403) {
              this.authorizationService.clearStorage();
              errorMessage = 'Session expired.';
            } else {
              errorMessage = error.error.message;
            }
            this.notificationService.pushNotification('Error', errorMessage);
        }
      });
    } else {
      this.notificationService.pushNotification('Error', 'No file selected.');
    }
    this.file = null;
  }

  public fileChange(event: Event) {
    const files = (event.target as HTMLInputElement).files;
    if(files != null && files.length > 0) {
      this.file = files[0];
    }
  }

}
