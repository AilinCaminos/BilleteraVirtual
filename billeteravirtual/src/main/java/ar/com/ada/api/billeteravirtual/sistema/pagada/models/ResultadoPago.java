package ar.com.ada.api.billeteravirtual.sistema.pagada.models;

public class ResultadoPago {

    public boolean isOk;
    public Integer idPago; // El id de lo que querramos devolver si queremos.
    public String message; // es el mensaje que le vamos a dar a front
    public String nombreEmpresa;
}