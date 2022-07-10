import { Component, OnInit } from '@angular/core';
import { Toast } from '../models/toast.model';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-toasts',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {

  toastsStack: Array<Toast> = [];

  constructor(private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.notificationService.getLastNotification().subscribe(notification => {
      if(notification) {
        this.pushToast(notification.title, notification.message, notification.date);
      }
    });
  }

  public getToasts() {
    return this.toastsStack;
  }

  public pushToast(title: string, message: string, date: Date) {
    this.toastsStack.push(new Toast(title, message, date));
  }

  public removeToast(toast: Toast) {
    const toastIdx = this.toastsStack.lastIndexOf(toast);
    this.toastsStack[toastIdx] = this.toastsStack[this.toastsStack.length - 1]
    this.toastsStack.pop();
  }

}
