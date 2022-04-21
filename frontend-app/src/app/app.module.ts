import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SigninComponent } from './signin/signin.component';
import { SignupComponent } from './signup/signup.component';
import { FileListComponent } from './file-list/file-list.component';
import { AccountComponent } from './account/account.component';
import { NavbarComponent } from './navbar/navbar.component';
import { AuthorizationSerice } from './services/authorization.service';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NotificationsComponent } from './notifications/notifications.component';
import { ToastComponent } from './toast/toast.component';
import { NotificationService } from './services/notification.service';
import { NgxPaginationModule } from 'ngx-pagination';
import { ApiCallerService } from './services/api-caller.service';


@NgModule({
  declarations: [
    AppComponent,
    SigninComponent,
    SignupComponent,
    FileListComponent,
    AccountComponent,
    NavbarComponent,
    NotificationsComponent,
    ToastComponent
    ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    FormsModule,
    HttpClientModule,
    NgxPaginationModule
  ],
  providers: [AuthorizationSerice, NotificationService, ApiCallerService],
  bootstrap: [AppComponent]
})
export class AppModule { }
