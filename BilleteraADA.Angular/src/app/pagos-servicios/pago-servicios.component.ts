import { Component, OnInit } from '@angular/core';
import { first } from 'rxjs/operators';

import { User } from '../_models';
import { UserService, AuthenticationService, BlService, AlertService } from '../_services';
import { Router } from '@angular/router';
import { Movimiento } from '../_models/bl/movimiento';
import { Saldo } from '../_models/bl/saldo';
import { Transferencia } from '../_models/bl/transferencia';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ServicioInfo, InfoPago } from '../_models/bl/pago-servicio';


@Component({ templateUrl: 'pago-servicios.component.html' })
export class PagoServiciosComponent implements OnInit {
    currentUser: User;
    servicios: ServicioInfo[];
    servicioAPagar: ServicioInfo;
    hacerPago: boolean;
    onPagando: boolean;
    servicioForm: FormGroup;

    constructor(
        private authenticationService: AuthenticationService,
        private blService: BlService,
        private userService: UserService,
        private router: Router,
        private alertService: AlertService,
        private formBuilder: FormBuilder

    ) {
        this.currentUser = this.authenticationService.currentUserValue;
    }

    ngOnInit() {


        this.servicioForm = this.formBuilder.group({

            codigoBarras: ['', Validators.required]

        });

    }

    onBuscarServicio(e: any) {

        this.blService.buscarServicioPorCodigoBarras(this.currentUser.billeteraId, this.servicioForm.get('codigoBarras').value)
            .pipe(first())
            .subscribe(
                data => {
                    console.log("trajo")
                    console.log(data);
                    if(data.length == 0) {
                        this.servicios = undefined;
                        this.alertService.error("No se encontro el servicio");
                        return;
                    }
                    this.servicios = data;

                },
                error => {
                    this.alertService.error(error);

                });
    }





    comenzarPago(servicio: ServicioInfo) {
        console.log("servicio a pagar");
        console.log(servicio)
        this.servicioAPagar = servicio;
        this.hacerPago = true;

    }
    onPagar(event: any) {


        if (!this.servicioForm.valid) {
            this.alertService.error("Faltan datos");
            return;
        }
        this.onPagando = true;
        let infoPago = new InfoPago;

        infoPago.fechaPago = new Date();
        infoPago.importePagado = this.servicioAPagar.importe;
        infoPago.medioPago = "ADADIGITAL";
        infoPago.infoMedioPago = "Billetera de " + this.currentUser.username;
        infoPago.moneda = this.servicioAPagar.moneda;



        this.blService.pagarServicio(this.currentUser.billeteraId, this.servicioAPagar.servicioId, infoPago)
            .pipe(first())
            .subscribe(
                data => {
                    this.alertService.success("Pago exitoso")
                    this.hacerPago = false;
                    this.onPagando = false;
                    this.servicioAPagar.estado = "PAGADO";
                    //this.loadData();
                },
                error => {
                    this.alertService.error(error);
                    this.onPagando = false;
                });
    }
    onCancelarPago(event: any) {
        this.hacerPago = false;

    }

}