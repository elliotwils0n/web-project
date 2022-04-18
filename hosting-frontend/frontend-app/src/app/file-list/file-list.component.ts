import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { File } from '../models/file.model';
import { AuthorizationSerice } from '../services/authorization.service';

@Component({
  selector: 'app-file-list',
  templateUrl: './file-list.component.html',
  styleUrls: ['./file-list.component.css']
})
export class FileListComponent implements OnInit {

  baseUrl: string = 'http://localhost:8080/api';
  files: File[] = []

  constructor(private httpClient: HttpClient, private authorizationService: AuthorizationSerice) { }

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
        this.authorizationService.refreshTokens();
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
        this.authorizationService.refreshTokens();
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
        this.authorizationService.refreshTokens();
      }
    });
  }

}
