import { Component, OnInit } from '@angular/core';
import { first } from 'rxjs/operators';

import { User } from '../_models';
import { UserService, AuthenticationService, BlService, AlertService } from '../_services';
import { Router } from '@angular/router';
import { Movimiento } from '../_models/bl/movimiento';
import { Saldo } from '../_models/bl/saldo';
import { Transferencia } from '../_models/bl/transferencia';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({ templateUrl: 'home.component.html' })
export class HomeComponent implements OnInit {
    currentUser: User;
    saldos: Saldo[];
    movimientos: Movimiento[];
    hacerTransferencia: boolean;
    onTransfiriendo:boolean;
    transferenciaForm: FormGroup;

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


        this.transferenciaForm = this.formBuilder.group({
            transferirA: ['', Validators.required],
            importeATransferir: ['', Validators.required],
            moneda: ['', Validators.required],
            concepto: ['', Validators.required],
            descripcion: ['', Validators.required]
        });
        this.loadData();
    }



    private loadData() {

        this.blService.getSaldos(this.currentUser.billeteraId)
            .pipe(first())
            .subscribe(
                data => {
                    this.saldos = data;
                    this.movimientos = undefined;
                },
                error => {
                    this.alertService.error(error);

                });



    }

    loadMovimientos(moneda: string) {
        this.blService.getMovimientos(this.currentUser.billeteraId, moneda)
            .pipe(first())
            .subscribe(
                data => {
                    this.movimientos = data;

                },
                error => {
                    this.alertService.error(error);

                });
    }

    comenzarTransferencia(moneda:string)
    {
        this.transferenciaForm.reset();
        //this.transferenciaForm['moneda'].value = moneda;
        this.transferenciaForm.get("moneda").setValue(moneda);
        this.transferenciaForm.get('importeATransferir').setValue(50);
        this.transferenciaForm.get('concepto').setValue("Varios");
        this.transferenciaForm.get('descripcion').setValue("Pago X");
        this.hacerTransferencia = true;

    }
    onTransferir(event: any) {


        if (!this.transferenciaForm.valid)
        {
            this.alertService.error("Faltan datos");
            return;
        }
        this.onTransfiriendo = true;
       
        let t = new Transferencia;
        t.email = this.transferenciaForm.get("transferirA").value;
        t.importe = this.transferenciaForm.get("importeATransferir").value;
        t.moneda = this.transferenciaForm.get("moneda").value;
        t.motivo = this.transferenciaForm.get("concepto").value;
        t.detalle = this.transferenciaForm.get("descripcion").value;


        this.blService.transferir(this.currentUser.billeteraId, t)
            .pipe(first())
            .subscribe(
                data => {
                    this.alertService.success("Transferencia exitosa")
                    this.hacerTransferencia = false;
                    this.onTransfiriendo = false;
                    this.loadData();
                },
                error => {
                    this.alertService.error(error);
                    this.onTransfiriendo = false;
                });
    }
    onCancelarTransferencia(event: any)
    {
        this.hacerTransferencia = false;
        
    }

}