import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { AuthorizationSerice } from "./authorization.service";

@Injectable()
export class ApiCallerService {

    baseUrl: string = 'http://localhost:8080/api';

    constructor(private authorizationService: AuthorizationSerice, private httpClient: HttpClient) { }

    public get(endPoint: string): Observable<any> {
        const headers = {'Authorization': this.authorizationService.getAccessToken()};
        return this.httpClient.get<any>(`${this.baseUrl}${endPoint}`, {headers: headers});
    }

    public getBlob(endPoint: string) {
        const headers = {'Authorization': this.authorizationService.getAccessToken()};
        return this.httpClient.get(`${this.baseUrl}${endPoint}`, {headers: headers, responseType: 'blob'})
    }

    public delete(endPoint: string) {
        const headers = {'Authorization': this.authorizationService.getAccessToken()};
        return this.httpClient.delete<any>(`${this.baseUrl}${endPoint}`, {headers: headers});
    }

    public post(endPoint: string, formData: FormData) {
        const headers = {'Authorization': this.authorizationService.getAccessToken()};
        return this.httpClient.post<any>(`${this.baseUrl}${endPoint}`, formData, {headers: headers})
    }
}