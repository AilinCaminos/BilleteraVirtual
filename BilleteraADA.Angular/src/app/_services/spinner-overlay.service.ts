import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { Injectable } from '@angular/core';
import { SpinnerOverlayComponent } from '../_components/spinner/spinner-overlay.component';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SpinnerOverlayService {
  private overlayRef: OverlayRef = null;
  public isLoadingHTTP = new BehaviorSubject(false);
  public disableLoading = false;

  constructor(private overlay: Overlay) { }

  public show(message = '') {
    // Returns an OverlayRef (which is a PortalHost)
    if (this.disableLoading)
        return;
    if (!this.overlayRef) {
      this.overlayRef = this.overlay.create();
    }

    // Create ComponentPortal that can be attached to a PortalHost
    const spinnerOverlayPortal = new ComponentPortal(SpinnerOverlayComponent);

    // run in async context for triggering "tick", thus avoid ExpressionChangedAfterItHasBeenCheckedError
    setTimeout(() => {
      if (this.overlayRef != null && !this.overlayRef.hasAttached()) {
        const component = this.overlayRef.attach(spinnerOverlayPortal); // Attach ComponentPortal to PortalHost
      }
      // TODO: set message
      // component.instance.message = message;
    });
  }

  public hide() {
    if (!!this.overlayRef) {
      this.overlayRef.detach(); 7
      this.overlayRef = null;
    }
  }
}
