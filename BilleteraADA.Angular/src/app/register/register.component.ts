import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';

import { AlertService, UserService, AuthenticationService } from '../_services';
import { stringify } from 'querystring';

import { RegisterInfo } from '../_models/registerInfo'

@Component({ templateUrl: 'register.component.html' })
export class RegisterComponent implements OnInit {
    registerForm: FormGroup;
    loading = false;
    submitted = false;
    
    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private authenticationService: AuthenticationService,
        private userService: UserService,
        private alertService: AlertService,
    ) {
        // redirect to home if already logged in
        if (this.authenticationService.currentUserValue) {
            this.router.navigate(['/']);
        }
    }

    ngOnInit() {
        this.registerForm = this.formBuilder.group({
            fullName: ['', Validators.required],
            dni: ['', Validators.required],
            email: ['', Validators.required],
            password: ['', [Validators.required, Validators.minLength(6)]],
            confirmPassword: ['', [Validators.required, Validators.minLength(6)]],
            
        });
    }

    // convenience getter for easy access to form fields
    get f() { return this.registerForm.controls; }

    public onSubmit(): void {
        this.registerUser();
    }

    registerUser() {
        this.submitted = true;

        // reset alerts on submit
        //this.alertService.clear();

        // stop here if form is invalid
        if (this.registerForm.invalid) {
            //this.submitted = false;
            return;
        }

        if ( this.f['password'].value !=  this.f['confirmPassword'].value) {
            this.alertService.error("Las contraseñas no coinciden");
            return;
        }
        
        this.loading = true;



        let ri = new RegisterInfo();

        ri.country = 32; //Argentina, deberia estar en el formulario
        ri.fullName = this.f['fullName'].value;
        ri.identificationType = 5; //DNI = 5 en la base de datos.
        ri.identification = this.f['dni'].value;
        ri.email = this.f['email'].value;
        ri.password = this.f['password'].value;
        ri.birthDate = new Date(); //solo para demo. deberia estar en el formulario


        this.userService.register(ri)
            .pipe(first())
            .subscribe(
                data => {
                    this.alertService.success('Registracion exitosa. Espera mail de confirmación!');
                    //this.router.navigate(['/home']);
                    this.loading = false;
                },
                error => {
                    this.alertService.error(error);
                    this.loading = false;
                });
    }
    
}
