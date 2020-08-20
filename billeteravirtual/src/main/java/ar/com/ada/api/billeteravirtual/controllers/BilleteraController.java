package ar.com.ada.api.billeteravirtual.controllers;

import java.security.Principal;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.com.ada.api.billeteravirtual.entities.Billetera;
import ar.com.ada.api.billeteravirtual.entities.Cuenta;
import ar.com.ada.api.billeteravirtual.entities.Transaccion;
import ar.com.ada.api.billeteravirtual.entities.Usuario;
import ar.com.ada.api.billeteravirtual.entities.Transaccion.ResultadoTransaccionEnum;
import ar.com.ada.api.billeteravirtual.models.request.CargaRequest;
import ar.com.ada.api.billeteravirtual.models.request.EnvioSaldoRequest;
import ar.com.ada.api.billeteravirtual.models.response.MovimientosResponse;
import ar.com.ada.api.billeteravirtual.models.response.SaldoResponse;
import ar.com.ada.api.billeteravirtual.models.response.TransaccionResponse;
import ar.com.ada.api.billeteravirtual.services.BilleteraService;
import ar.com.ada.api.billeteravirtual.services.UsuarioService;
import ar.com.ada.api.billeteravirtual.sistema.pagada.models.InfoPago;
import ar.com.ada.api.billeteravirtual.sistema.pagada.models.ResultadoPago;
import ar.com.ada.api.billeteravirtual.sistema.pagada.models.Servicio;

@RestController
public class BilleteraController {

    @Autowired
    BilleteraService billeteraService;
    @Autowired
    UsuarioService usuarioService;

    /*
     * webMetodo 1: consultar saldo: GET URL:/billeteras/{id}/saldos
     * URL:/billeteras/{id}/saldos/{moneda} webMetodo 2: cargar saldo: POST
     * URL:/billeteras/{id}/recargas requestBody: { "moneda": "importe": } webMetodo
     * 3: * enviar saldo: POST URL:/billetera/{id}/envios requestBody: { "moneda":
     * "importe": "email": "motivo": "detalleDelMotivo": }
     */

    // Metodo de verificacion 1 del checkeo que el usuario que consulta sea el que
    // corresponde
    // Usamos el "Principal": que es una abstraccion que nos permite acceder al
    // usuario que esta logueado
    @GetMapping("/billeteras/{id}/saldos/{moneda}")
    public ResponseEntity<?> consultarSaldo(Principal principal, @PathVariable Integer id,
            @PathVariable String moneda) {

        // Obtengo primero el usuario en base al Principal
        Usuario usuarioLogueado = usuarioService.buscarPorUsername(principal.getName());
        // Checkqueo si la billetera le corresponde, si no, 403 Forbiden
        // Esto deberia hacerlo en cada metodo
        if (!usuarioLogueado.getPersona().getBilletera().getBilleteraId().equals(id)) {
            // Generar una alerta de cyberseguridad

            // Es responder un resultado mentiroso: ej 404
            return ResponseEntity.status(403).build();// Forbideen
        }

        SaldoResponse saldo = new SaldoResponse();

        saldo.saldo = billeteraService.consultarSaldo(id, moneda);
        saldo.moneda = moneda;

        return ResponseEntity.ok(saldo);
    }

    // Metodo Verificacion Billetera 2: haciendo lo mismo que antes, pero usando
    // Spring Expression LANGUAGE(magic)
    // Aca el principal es el User, este principal no es el mismo principal del
    // metodo anterior
    // pero apunta a uno parecido(el de arriba es el principal authentication)
    // https://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html
    @GetMapping("/billeteras/{id}/saldos")
    @PreAuthorize("@usuarioService.buscarPorUsername(principal.getUsername()).getPersona().getBilletera().getBilleteraId().equals(#id)")
    public ResponseEntity<List<SaldoResponse>> consultarSaldo(@PathVariable Integer id) {

        Billetera billetera = new Billetera();

        billetera = billeteraService.buscarPorId(id);

        List<SaldoResponse> saldos = new ArrayList<>();

        for (Cuenta cuenta : billetera.getCuentas()) {

            SaldoResponse saldo = new SaldoResponse();

            saldo.saldo = cuenta.getSaldo();
            saldo.moneda = cuenta.getMoneda();
            saldos.add(saldo);
        }
        return ResponseEntity.ok(saldos);
    }

    /**
     * webMetodo 2: cargar saldo: POST URL:/billeteras/{id}/recargas requestBody: {
     * "moneda": "importe": } webMetodo
     */
    @PostMapping("/billeteras/{id}/recargas")
    public ResponseEntity<TransaccionResponse> cargarSaldo(@PathVariable Integer id,
            @RequestBody CargaRequest recarga) {

        TransaccionResponse response = new TransaccionResponse();

        billeteraService.cargarSaldo(recarga.importe, recarga.moneda, id, "recarga", "porque quiero");

        response.isOk = true;
        response.message = "Cargaste saldo exisotasamente";

        return ResponseEntity.ok(response);

    }

    /***
     * enviar saldo: POST URL:/billeteras/{id}/envios requestBody: { "moneda":
     * "importe": "email": "motivo": "detalleDelMotivo": }
     */

    @PostMapping("/billeteras/{id}/envios")
    public ResponseEntity<TransaccionResponse> enviarSaldo(@PathVariable Integer id,
            @RequestBody EnvioSaldoRequest envio) {

        TransaccionResponse response = new TransaccionResponse();
        ResultadoTransaccionEnum resultado = billeteraService.enviarSaldo(envio.importe, envio.moneda, id, envio.email,
                envio.motivo, envio.detalle);

        if (resultado == ResultadoTransaccionEnum.INICIADA) {
            response.isOk = true;
            response.message = "Se envio el saldo exitosamente";

            return ResponseEntity.ok(response);
        }
        response.isOk = false;
        response.message = "Hubo un error al realizar la operacion " + resultado;

        return ResponseEntity.badRequest().body(response);

    }

    // Metodo Verificacion Billetera 3: haciendo lo mismo que antes, pero leyendo
    // desde el el authority. O sea , cuando creamos el User para el UserDetails(no
    // el usuario)
    // Le seteamos una autoridad sobre la billetera X.
    // Esto lo que hace es preguntar si tiene esa autoridad seteada.
    // Dentro de este, tenemos 2 formas de llenar el Authority
    // Llenandolo desde la Base de datos, o desde el JWT
    // Desde la DB nos da mas seguridad pero cada vez que se ejecute es ir a buscar
    // a la DB
    // Desde el JWT, si bien exponemos el billeteraId, nos permite evitarnos ir a la
    // db.
    // Este CLAIM lo podemos hacer con cualquier propiedad que querramos mandar
    // al JWT

    @GetMapping("/billeteras/{id}/movimientos/{moneda}")
    @PreAuthorize("hasAuthority('CLAIM_billeteraId_'+#id)")
    public ResponseEntity<List<MovimientosResponse>> consultarMovimientos(Authentication prinicpal,
            @PathVariable Integer id, @PathVariable String moneda) {

        Billetera billetera = new Billetera();
        billetera = billeteraService.buscarPorId(id);
        List<Transaccion> trancciones = billeteraService.listarTransacciones(billetera, moneda);
        List<MovimientosResponse> res = new ArrayList<>();

        for (Transaccion transaccion : trancciones) {

            MovimientosResponse movimiento = new MovimientosResponse();
            movimiento.numeroDeTransaccion = transaccion.getTransaccionId();
            movimiento.fecha = transaccion.getFecha();
            movimiento.importe = transaccion.getImporte();
            movimiento.moneda = transaccion.getMoneda();
            movimiento.conceptoOperacion = transaccion.getConceptoOperacion();
            movimiento.tipoOperacion = transaccion.getTipoOperacion();
            movimiento.detalle = transaccion.getDetalle();
            movimiento.aUsuario = usuarioService.buscarPor(transaccion.getaUsuarioId()).getEmail();

            res.add(movimiento);
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/billeteras/{id}/movimientos")
    public ResponseEntity<List<MovimientosResponse>> consultarMovimientos(@PathVariable Integer id) {

        Billetera billetera = new Billetera();
        billetera = billeteraService.buscarPorId(id);
        List<Transaccion> trancciones = billeteraService.listarTransacciones(billetera);
        List<MovimientosResponse> res = new ArrayList<>();

        for (Transaccion transaccion : trancciones) {

            MovimientosResponse movimiento = new MovimientosResponse();
            movimiento.numeroDeTransaccion = transaccion.getTransaccionId();
            movimiento.fecha = transaccion.getFecha();
            movimiento.importe = transaccion.getImporte();
            movimiento.moneda = transaccion.getMoneda();
            movimiento.conceptoOperacion = transaccion.getConceptoOperacion();
            movimiento.tipoOperacion = transaccion.getTipoOperacion();
            movimiento.detalle = transaccion.getDetalle();
            movimiento.aUsuario = usuarioService.buscarPor(transaccion.getaUsuarioId()).getEmail();

            res.add(movimiento);
        }
        return ResponseEntity.ok(res);
    }

    // LISTA SOLO por codigo de barras.
    @GetMapping("/billeteras/{id}/servicios")
    @PreAuthorize("hasAuthority('CLAIM_billeteraId_'+#id)")
    public ResponseEntity<List<Servicio>> buscarServicio(@PathVariable Integer id,
            @RequestParam(name = "codigo", required = true) String codigoBarras) {

        List<Servicio> servicios = new ArrayList<>();

        servicios = billeteraService.buscarServicioPorCodigoDeBarras(codigoBarras);

        return ResponseEntity.ok(servicios);

    }

    /**
     * Integracion de pagaar un servicio a traves de la billetera virtual hacia el
     * sistema de pagADA
     */

    @PostMapping("/billeteras/{id}/servicios/{servicioId}")
    @PreAuthorize("hasAuthority('CLAIM_billeteraId_'+#id)")
    public ResponseEntity<ResultadoPago> pagarServicio(@PathVariable Integer id, 
                    @PathVariable Integer servicioId,
                    @RequestBody InfoPago pago) {

        ResultadoPago r = billeteraService.pagarServicio(id,servicioId, pago);

        if (r.isOk) {
            return ResponseEntity.ok(r);
        } else {
            return ResponseEntity.badRequest().body(r);
        }
    }

}