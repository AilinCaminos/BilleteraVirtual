import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { User } from '../_models';
import { PerfilUsuario } from '../_models/perfilUsuario';

import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { formatDate } from '@angular/common';
import { Saldo } from '../_models/bl/saldo';
import { DepositoResult, Deposito } from '../_models/bl/deposito';
import { Transferencia, TransferenciaResult } from '../_models/bl/transferencia';
import { Movimiento } from '../_models/bl/movimiento';
import { ServicioInfo, InfoPago, PagoServicioResult } from '../_models/bl/pago-servicio';


@Injectable({ providedIn: 'root' })
export class BlService {
    constructor(private http: HttpClient) { }

    getSaldos(id: string) {
        return this.http.get<Saldo[]>(`${environment.apiUrl}/billeteras/${id}/saldos`);
    }

    getMovimientos(id: string, moneda: string) {
        return this.http.get<Movimiento[]>(`${environment.apiUrl}/billeteras/${id}/movimientos/${moneda}`);
    }

    depositar(id: string, d: Deposito) {
        return this.http.post<DepositoResult>(`${environment.apiUrl}/billeteras/${id}/recargas`, d);
    }

    transferir(id: string, t: Transferencia) {
        return this.http.post<TransferenciaResult>(`${environment.apiUrl}/billeteras/${id}/envios`, t);
    }


    buscarServicioPorCodigoBarras(id: string, codigoBarras: string) {
        return this.http.get<ServicioInfo[]>(`${environment.apiUrl}/billeteras/${id}/servicios?codigo=${codigoBarras}`);
    }

    pagarServicio(billeteraId: string, servicioId: number, p: InfoPago) {
        return this.http.post<PagoServicioResult>(`${environment.apiUrl}/billeteras/${billeteraId}/servicios/${servicioId}`, p);
    }
}