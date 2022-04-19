import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { timer, Observable, Subscription } from "rxjs";
import { Credentials } from "../models/credentials.model";
import { NotificationService } from "./notification.service";


@Injectable()
export class AuthorizationSerice implements CanActivate {

    private timerSubscription: Subscription | undefined;
    private timer: Observable<Number> = timer(300000, 300000);

    sessionActive: string = 'sessionActive';
    accessToken: string = 'accessToken';
    refreshToken: string = 'refreshToken';
    
    baseUrl: string = 'http://localhost:8080/api';

    
    constructor(private notificationService: NotificationService, private httpClient: HttpClient, private router: Router) {}


    public canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
        if(this.isSessionActive()) {
            return true;
        }
        this.router.navigateByUrl('/signin')
        return false;
    }

    public isSessionActive() {

        return localStorage.getItem(this.sessionActive) != null && localStorage.getItem(this.sessionActive) == 'true';
    }

    public getAccessToken() {
        return `Bearer ${localStorage.getItem(this.accessToken)}`;
    }

    public generateTokens(credentials: Credentials) {
        this.httpClient
        .post<any>(`${this.baseUrl}/auth/generateToken`, {'username': credentials.username, 'password': credentials.password})
        .subscribe({
            next: data => {
                localStorage.setItem(this.accessToken, data.accessToken);
                localStorage.setItem(this.refreshToken, data.refreshToken);
                localStorage.setItem(this.sessionActive, 'true');
                this.router.navigateByUrl('/files');

                this.timerSubscription = this.timer.subscribe(x => {
                    this.refreshTokens();
                });
            },
            error: error => {
                this.notificationService.pushNotification('Error', 'Provided username and/or password invalid.');
                console.log(error.error);
            }
        });
    }

    public refreshTokens() {
        console.log('refreshing tokens...');
        const headers = {'Authorization': `Bearer ${localStorage.getItem(this.refreshToken)}`};
        this.httpClient
        .post<any>(`${this.baseUrl}/auth/refreshToken`, null, {'headers':headers})
        .subscribe({
            next: data => {
                localStorage.setItem(this.accessToken, data.accessToken);
                localStorage.setItem(this.refreshToken, data.refreshToken);
                localStorage.setItem(this.sessionActive, 'true');
            },
            error: error => {
                this.notificationService.pushNotification('Error', 'Error occurred while trying to refresh tokens.');
                this.clearStorage();
            }
        });
    }

    public clearStorage() {
        if(this.timerSubscription) {
            this.timerSubscription.unsubscribe();
        }
        localStorage.removeItem(this.accessToken);
        localStorage.removeItem(this.refreshToken);
        localStorage.removeItem(this.sessionActive);
        this.router.navigateByUrl('/signin');
    }

}