import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { Toast } from "../models/toast.model";

@Injectable()
export class NotificationService {

    private notification = new BehaviorSubject<Toast | null>(null);

    constructor() { }

    public pushNotification(title: string, message: string) {
        this.notification.next(new Toast(title, message, new Date()));
    }

    public getLastNotification() : Observable<Toast | null> {
        return this.notification.asObservable();
    }
}