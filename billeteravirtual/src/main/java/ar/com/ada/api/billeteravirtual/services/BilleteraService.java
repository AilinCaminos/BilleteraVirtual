package ar.com.ada.api.billeteravirtual.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.ada.api.billeteravirtual.entities.Billetera;
import ar.com.ada.api.billeteravirtual.repositories.BilleteraRepository;

@Service
public class BilleteraService {

    @Autowired
    BilleteraRepository billeteraRepository;

    public void grabar(Billetera billetera){
        billeteraRepository.save(billetera);
    }
    
    /**
     * Metodo cargarSaldo 
     * buscar billetera por id 
     * se identifica cuenta por moneda
     * determinar importe a cargar 
     * hacer transaccion
     * 
     * ver delegaciones sobre entidades
     * 
     */

    /**
     * Metodo enviarSaldo 
     * buscar billetera por id 
     * se identifica cuenta por moneda
     * determinar importe a transferir
     * billetera de origen y billetera destino
     * actualizar los saldos de las cuentas (resta en la origen y suma en la destino)
     * generar 2 transacciones
     * 
     * ver delegaciones sobre entidades
     * 
     */

     /**
      * Metodo consultarSaldo
      * buscar billetera por id
      * se identifica cuenta por moneda
      * traer saldo
      */


     
}