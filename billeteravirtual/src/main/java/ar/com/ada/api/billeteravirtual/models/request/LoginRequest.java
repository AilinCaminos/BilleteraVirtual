package ar.com.ada.api.billeteravirtual.models.request;

import javax.validation.constraints.NotBlank;

/**
 * LoginRequest
 */
public class LoginRequest {
    @NotBlank(message = "username es obligatorio")
    public String username;
    @NotBlank(message = "password es obligatorio")
    public String password;
}