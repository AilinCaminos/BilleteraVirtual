import { BrowserModule } from '@angular/platform-browser';
import { NgModule, enableProdMode, LOCALE_ID } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';


// used to create fake backend
//import { fakeBackendProvider } from './_helpers';
import { AppRoutingModule } from './app-routing.module';
import { JwtInterceptor, ErrorInterceptor, LoaderInterceptor } from './_helpers';
import { AppComponent } from './app.component';
import { ButtonsModule } from '@progress/kendo-angular-buttons';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { InputsModule } from '@progress/kendo-angular-inputs';
import { GridModule } from '@progress/kendo-angular-grid';
import { DialogsModule, DialogModule } from '@progress/kendo-angular-dialog';
import { DropDownsModule } from '@progress/kendo-angular-dropdowns';
import { LabelModule } from '@progress/kendo-angular-label';
import { DateInputsModule } from '@progress/kendo-angular-dateinputs';
import { HomeComponent } from './home';
import { LoginComponent } from './login';
import { RegisterComponent } from './register';
import { AlertComponent } from './_components';


import { LayoutModule } from '@progress/kendo-angular-layout';
import { UploadModule } from '@progress/kendo-angular-upload';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { FormsModule } from '@angular/forms';

import { IntlModule } from '@progress/kendo-angular-intl';

import '@progress/kendo-angular-intl/locales/es-AR/all';

import { registerLocaleData } from '@angular/common';
import localeEsAr from '@angular/common/locales/es-AR';



import { ChartsModule } from '@progress/kendo-angular-charts';
import 'hammerjs';
import { ToastrModule } from 'ngx-toastr';
import { SpinnerComponent } from './_components/spinner/spinner.component';
import { SpinnerOverlayComponent } from './_components/spinner/spinner-overlay.component';
import { SpinnerOverlayService } from './_services/spinner-overlay.service';
import { Overlay } from '@angular/cdk/overlay';
import { TooltipModule } from '@progress/kendo-angular-tooltip';
import { PagoServiciosComponent } from './pagos-servicios';

registerLocaleData(localeEsAr);

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    RegisterComponent,
    PagoServiciosComponent,
    AlertComponent,
    SpinnerComponent,
    SpinnerOverlayComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ButtonsModule,
    BrowserAnimationsModule,
    InputsModule,
    GridModule,
    DialogsModule,
    DropDownsModule,
    LabelModule,
    DateInputsModule,
    ReactiveFormsModule,
    HttpClientModule,
    LayoutModule,
    UploadModule,
    FormsModule,
    DialogModule,
    NgbModule,

    
    IntlModule,

    ChartsModule,

    ToastrModule.forRoot({
      timeOut: 5000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
      progressBar: true,
    }),

    TooltipModule,

  ],

  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: LoaderInterceptor, multi: true },
    // provider used to create fake backend
    //fakeBackendProvider
    //recaptcha v3
    { provide: LOCALE_ID, useValue: 'es-AR' },
    SpinnerOverlayService,
    Overlay
  ],
  bootstrap: [AppComponent],
  entryComponents: [SpinnerOverlayComponent]
})
export class AppModule { };