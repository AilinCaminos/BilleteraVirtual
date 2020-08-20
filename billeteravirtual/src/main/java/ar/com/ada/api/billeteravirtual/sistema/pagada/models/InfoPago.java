package ar.com.ada.api.billeteravirtual.sistema.pagada.models;

import java.math.BigDecimal;
import java.util.Date;

import kong.unirest.json.*;

/**
 * Este payload es el que usa la billetera para pagar Puede o no ser parecido al
 * de pagADA
 * 
 */
public class InfoPago {

    private BigDecimal importePagado;
    private Date fechaPago;
    private String medioPago;
    private String infoMedioPago;
    private String moneda;

    public BigDecimal getImportePagado() {
        return importePagado;
    }

    public void setImportePagado(BigDecimal importePagado) {
        this.importePagado = importePagado;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getInfoMedioPago() {
        return infoMedioPago;
    }

    public void setInfoMedioPago(String infoMedioPago) {
        this.infoMedioPago = infoMedioPago;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

	

    
}