package ar.com.ada.api.billeteravirtual.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.ada.api.billeteravirtual.entities.Persona;
import ar.com.ada.api.billeteravirtual.entities.Usuario;
@Service
public class UsuarioService {

    @Autowired
    PersonaService personaService;
    
	public Usuario buscarPorUsername(String username) {
		return null;
	}

	public void login(String username, String password) {
	}

     /**Metodo IniciarSesion
      * recibe usuario y contraseña
      * validar usuario y contraseña
      */

    public Usuario crearUsuario(String nombre, int pais, int tipoDocumento, String documento, Date fechaNacimiento, String email, String password) {

    /**Metodo para crearUsuario
     * 1 crear persona (se le settea un usuario)
     * 2 crear usuario
     * 3 crear billetera 
     * 4 crear cuenta por moneda(ARS y/o USD?)
     */
    
      Persona persona = new Persona();
        persona.setNombre(nombre);
        persona.setPaisId(pais);
        persona.setTipoDocumentoId(tipoDocumento);
        persona.setDocumento(documento);
        persona.setFechaNacimiento(fechaNacimiento);

        Usuario usuario = new Usuario();
        usuario.setUsername(email);
        usuario.setEmail(email);
        usuario.setPassword(password);

        persona.setUsuario(usuario);

        personaService.grabar(persona);

        return usuario;
    }
}