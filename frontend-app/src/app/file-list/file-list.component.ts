import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FileInfo } from '../models/file-info.model';
import { NotificationService } from '../services/notification.service';
import { saveAs } from 'file-saver';
import { ApiCallerService } from '../services/api-caller.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-file-list',
  templateUrl: './file-list.component.html',
  styleUrls: ['./file-list.component.css']
})
export class FileListComponent implements OnInit {
  
  @ViewChild('inputFile') inputFile: ElementRef | undefined;

  page = 1;
  files: FileInfo[] = []
  file: File | null = null;
  InputVar: ElementRef | null = null;

  constructor(private apiCallerSerivce: ApiCallerService, private notificationService: NotificationService, private router: Router) { }

  ngOnInit(): void {
    this.page = 1;
    this.refreshFileList();
  }

  public refreshFileList() {
    this.apiCallerSerivce.get('/files/list').subscribe({
      next: data => {
        this.files = data;
      },
      error: error => {
        const errorMessage = error.error.message ? error.error.message : 'Something went wrong.';
        this.notificationService.pushNotification('Error', errorMessage);
        if(error.error.status == 401) {
          localStorage.clear();
          this.router.navigateByUrl('/signin');
        }
      }
    });
  }

  public downloadFile(id: number, filename: string) {
    this.apiCallerSerivce.getBlob(`/files/download/${id}`).subscribe({
      next: data => {
        saveAs(new Blob([data]), filename);
        this.notificationService.pushNotification('Success', 'File downloaded successfully.');
      },
      error: error => {
        const errorMessage = error.error.message ? error.error.message : 'Something went wrong.';
        this.notificationService.pushNotification('Error', errorMessage);
        if(error.error.status == 401) {
          localStorage.clear();
          this.router.navigateByUrl('/signin');
        }
      }
    });
  }

  public deleteFile(id: number) {
    this.apiCallerSerivce.delete(`/files/delete/${id}`).subscribe({
      next: data => {
        this.refreshFileList();
        this.notificationService.pushNotification('Success', 'File deleted successfully');
      },
      error: error => {
        const errorMessage = error.error.message ? error.error.message : 'Something went wrong.';
        this.notificationService.pushNotification('Error', errorMessage);
        if(error.error.status == 401) {
          localStorage.clear();
          this.router.navigateByUrl('/signin');
        }
      }
    });
  }

  public uploadFile() {
   
    if(this.file) {
        const formData = new FormData();
        formData.append('file', this.file);

        this.apiCallerSerivce.post('/files/upload', formData).subscribe({
          next: data => {
            this.refreshFileList();
            this.notificationService.pushNotification('Success', 'File uploaded Successfully.');
          },
          error: error => {
            const errorMessage = error.error.message ? error.error.message : 'Something went wrong.';
            this.notificationService.pushNotification('Error', errorMessage);
            if(error.error.status == 401) {
              localStorage.clear();
              this.router.navigateByUrl('/signin');
            }
        }
      });
    } else {
      this.notificationService.pushNotification('Error', 'No file selected.');
    }
    this.file = null;
    this.clearInput();
  }

  public fileChange(event: Event) {
    const files = (event.target as HTMLInputElement).files;
    if(files != null && files.length > 0) {
      this.file = files[0];
    }
  }

  public clearInput() {
    if(this.inputFile){
      this.inputFile.nativeElement.value = null;
    }
  }

}
