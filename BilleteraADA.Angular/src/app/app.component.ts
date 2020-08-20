import { Component } from '@angular/core';
import { Router, NavigationStart, NavigationEnd, NavigationCancel } from '@angular/router';

import { AuthenticationService, AlertService } from './_services';
import { User } from './_models';

import './_content/app.less';
import { SpinnerOverlayService } from './_services/spinner-overlay.service';

@Component({
    selector: 'app', templateUrl: 'app.component.html',
    styleUrls: ['app.component.scss']
})
export class AppComponent {
    currentUser: User;

    constructor(
        private router: Router,
        private authenticationService: AuthenticationService,
        private spinnerService: SpinnerOverlayService,
        private alertService: AlertService
    ) {
        this.authenticationService.currentUser.subscribe(x => this.currentUser = x);
        this.router.events.subscribe(event => {
            if (event instanceof NavigationStart) {
                this.spinnerService.isLoadingHTTP.next(false);
                this.spinnerService.show();
            }
            else if (event instanceof NavigationCancel) {
                this.spinnerService.hide();
                
                
            }
            else if (event instanceof NavigationEnd) {
                this.spinnerService.hide();
            }
        });

    }

    logout() {
        this.authenticationService.logout();
        this.router.navigate(['/login']);
    }
}
