export class Deposito {

    moneda: string;
    importeADepositar: number;
    tipo: string;
    concepto: string;
    detalle: string;

}

export class DepositoResult {
    isOk: boolean;
    message: string;
}