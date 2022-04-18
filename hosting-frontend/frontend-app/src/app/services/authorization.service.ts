import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import { Credentials } from "../models/credentials.model";


@Injectable()
export class AuthorizationSerice implements CanActivate {
    
    baseUrl: string = 'http://localhost:8080/api';
    sessionActive: string = 'sessionActive';
    accessToken: string = 'accessToken';
    refreshToken: string = 'refreshToken';
    
    constructor(private httpClient: HttpClient, private router: Router){}

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
                localStorage.setItem(this.sessionActive, 'true');
                localStorage.setItem(this.accessToken, data.accessToken);
                localStorage.setItem(this.refreshToken, data.refreshToken);
                this.router.navigateByUrl('/files');
            },
            error: error => {
                console.log(error.error);
            }
        });
    }

    public refreshTokens() {
        const headers = {'Authorization': `Bearer ${localStorage.getItem(this.refreshToken)}`};
        this.httpClient
        .post<any>(`${this.baseUrl}/auth/refreshToken`, null, {'headers':headers})
        .subscribe({
            next: data => {
                localStorage.setItem(this.sessionActive, 'true');
                localStorage.setItem(this.accessToken, data.accessToken);
                localStorage.setItem(this.refreshToken, data.refreshToken);
            },
            error: error => {
                this.clearStorage();
            }
        });
    }

    public clearStorage() {
        localStorage.removeItem(this.sessionActive);
        localStorage.removeItem(this.accessToken);
        localStorage.removeItem(this.refreshToken);
        this.router.navigateByUrl('/signin');
    }

}