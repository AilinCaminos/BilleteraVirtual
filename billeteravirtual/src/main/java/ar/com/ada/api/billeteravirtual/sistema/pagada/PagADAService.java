package ar.com.ada.api.billeteravirtual.sistema.pagada;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.*;

import com.google.gson.JsonArray;

import ar.com.ada.api.billeteravirtual.sistema.pagada.models.*;
import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import java.text.*;

@Service
/***
 * Esta clase tiene toda la implementacion de la llamada a la API de PagADA La
 * autorizacion de ejecucion de la API esta dada por una API Key que nos dio
 * pagADA en algun momento(una unica vez)
 */
public class PagADAService {

    public enum MedioPagoEnum {
        TARJETA, TRANSFERENCIA, DEPOSITO, ADADIGITAL
    }

    // Settings de la api

    // ApiKey: la usamos para conectarnos a la api de pagADA
    @Value("${pagADASettings.apiKey}")
    private String apiKey;
    // URL base
    @Value("${pagADASettings.apiBaseUri}")
    public String apiBaseUri;

    // Habilitado o no
    @Value("${pagADASettings.enabled}")
    public boolean enabled;

    public List<Servicio> buscarServicioPorCodigoDeBarras(String codigoBarras) {

        List<Servicio> servicios = new ArrayList<>();

        JsonNode r;
        HttpResponse<JsonNode> request = Unirest.get(apiBaseUri + "/api/servicios")
                .queryString("codigo", codigoBarras)
                .header("api", this.apiKey).asJson();

        r = request.getBody();

        // Como devuelve una lista de servicios en formato json
        JSONArray jsonArray = r.getArray();

        // Mapeo cada campo del JSON ARRAY que devolvio, a un objeto Servicio
        // Esto se puede hacer con tools de mampings automagicas, pero se deja en modo
        // procedural solo para educacional(igual a veces no vale la pena hacer un
        // mapping)
        for (int i = 0; i < jsonArray.length(); i++) {
            Servicio servicio = new Servicio();

            JSONObject jsonObject = jsonArray.getJSONObject(i);

            servicio.setServicioId(jsonObject.getInt("servicioId"));
            servicio.setCodigoBarras(jsonObject.getString("codigoBarras"));
            servicio.setEmpresa(jsonObject.getJSONObject("empresa").getString("nombre"));
            servicio.setTitular(jsonObject.getJSONObject("deudor").getString("nombre"));
            servicio.setImporte(jsonObject.getBigDecimal("importe"));
            servicio.setMoneda(jsonObject.getString("moneda"));
            servicio.setEstado(jsonObject.getString("estadoId"));

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                servicio.setFechaEmision(df.parse(jsonObject.getString("fechaEmision")));
                servicio.setFechaVencimiento(df.parse(jsonObject.getString("fechaVencimiento")));

            } catch (Exception ex) {
                // DEBERIA TIRAR ERROR porque nose formateo la fecha.
            }

            servicio.setNumero(jsonObject.getString("numero"));
            servicio.setTipoServicio(jsonObject.getJSONObject("tipoServicio").getString("nombre"));

            servicios.add(servicio);
        }

        return servicios;
    }

    // Desde este service llamamos a otra API
    public ResultadoPago pagarServicio(Integer servicioId, InfoPago infoPago) {

        ResultadoPago resultadoPago = new ResultadoPago();
        JsonNode r;
        // En este caso es un POST a /api/servicios/id

        String infoPagoStr = JSONObject.valueToString(infoPago);

        

        JSONObject infoPagoJson = new JSONObject(infoPagoStr);

        //Cambio el formato de fechas porqeu el JSON tiene que ser en forma
        //YYYY-MM-DD
        //Esto se puede hacer con una dependencia(ej la de GSON) pero lo hago "manualmente"

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        infoPagoJson.put("fechaPago", formatter.format(infoPago.getFechaPago()));

        HttpResponse<JsonNode> request = Unirest.post(apiBaseUri + "/api/servicios/{id}")
                .routeParam("id", servicioId.toString()) //Este es el PATHVariable
                .header("content-type", "application/json")
                .body(infoPagoJson) //AcaPasamos el RequestBody
                .header("api", this.apiKey).asJson();

        //Si es un error que no sabemos
        if (request.getStatus() != 200 && request.getStatus() != 400) {
            resultadoPago.isOk = false;
            resultadoPago.message = "Error inesperado " + request.getStatus();
            return resultadoPago;
        }
        r = request.getBody();

        
        // Este es el GenericResponse de la api
        JSONObject jsonObject = r.getObject();

        if (jsonObject.getBoolean("isOk")) {
            // Esta todo bien
            //Esto de abajo se puede hacer con un mapper, llenando el
            //resultadoPago con los valores del Json
            //ahora se hace manual solo a titulo educativo.
            resultadoPago.isOk = true;
            resultadoPago.idPago = jsonObject.getInt("id");
            resultadoPago.message = jsonObject.getString("message");
            resultadoPago.nombreEmpresa = jsonObject.getString("nombreEmpresa");
        } else {
            resultadoPago.isOk = false;
            resultadoPago.message = jsonObject.getString("message");
        }
        return resultadoPago;
    }
}