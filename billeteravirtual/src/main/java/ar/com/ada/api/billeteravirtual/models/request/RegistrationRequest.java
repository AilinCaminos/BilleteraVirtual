package ar.com.ada.api.billeteravirtual.models.request;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * RegistrationRequest
 */
public class RegistrationRequest {

    @NotBlank(message = "el nombre no puede ser nulo")
    public String fullName; //Nombre persona
    @Min(1)
    public int country; //pais del usuario
    public int identificationType; //Tipo Documento
    public String identification; //nro documento
    public Date birthDate; //fechaNacimiento
    public String email; //email
    @NotBlank(message = "La contraseña debe tener al menos 8 digitos")
    public String password; //contraseña elegida por el usuario.
    
}