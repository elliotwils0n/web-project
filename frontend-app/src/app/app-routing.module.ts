import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountComponent } from './account/account.component';
import { FileListComponent } from './file-list/file-list.component';
import { AuthorizationSerice } from './services/authorization.service';
import { SigninComponent } from './signin/signin.component';
import { SignupComponent } from './signup/signup.component';

const routes: Routes = [
  {path: '', redirectTo: 'files', pathMatch: 'full'},
  {path: 'files', component: FileListComponent, canActivate: [AuthorizationSerice]},
  {path: 'account', component: AccountComponent, canActivate: [AuthorizationSerice]},
  {path: 'signin', component: SigninComponent},
  {path: 'signup', component: SignupComponent},
  {path: '**', redirectTo: 'files', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
