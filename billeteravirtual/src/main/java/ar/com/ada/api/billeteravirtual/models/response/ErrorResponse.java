package ar.com.ada.api.billeteravirtual.models.response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.BindingResult;

public class ErrorResponse {

    public String message;
    public List<ErrorResponseItem> errors = new ArrayList<>();

    public static ErrorResponse FromBindingResults(BindingResult results) {
        ErrorResponse err = new ErrorResponse();
        err.message = "Ocurrieron errores al validar el modelo de datos";
        results.getFieldErrors().stream().forEach(e -> {
        
            err.errors.add(new ErrorResponseItem(e.getField(), e.getDefaultMessage()));
        });
        return err;
    }

}
