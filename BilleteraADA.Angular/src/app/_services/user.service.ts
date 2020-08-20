import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { User, BuscaUsuarios } from '../_models';
import { PerfilUsuario } from '../_models/perfilUsuario';

import { environment } from '../../environments/environment';
import { RegisterInfo } from '../_models/registerInfo';

@Injectable({ providedIn: 'root' })
export class UserService {
    constructor(private http: HttpClient) { }


    register(regInfo: RegisterInfo) {
        return this.http.post(`${environment.apiUrl}/auth/register`, regInfo);
    }
    
}