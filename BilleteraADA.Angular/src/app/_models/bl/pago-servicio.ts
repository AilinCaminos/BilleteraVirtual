export class InfoPago {


    importePagado: number;
    fechaPago: Date;
    medioPago: string;
    infoMedioPago: string;
    moneda: string;

}

export class ServicioInfo {
    servicioId: number;
    empresa: string;
    titular: string;
    tipoServicio: string;
    numero: string;
    moneda: string;
    fechaEmision: string;
    fechaVencimiento: string;
    importe: number;
    codigoBarras: string;
    estado: string;
}

export class PagoServicioResult {
    isOk: boolean;
    id: number;
    message: string;
    empresa: string
}