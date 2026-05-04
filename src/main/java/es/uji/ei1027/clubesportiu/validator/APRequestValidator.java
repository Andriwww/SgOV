package es.uji.ei1027.clubesportiu.validator;

import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.Estado;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class APRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return APRequest.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        APRequest request = (APRequest) obj;

        // idUsuario obligatorio
        if (request.getIdUsuario() <= 0) {
            errors.rejectValue("idUsuario", "obligatorio", "El usuario es obligatorio");
        }

        // fechaSolicitud obligatoria
        if (request.getFechaSolicitud() == null) {
            errors.rejectValue("fechaSolicitud", "obligatorio", "La fecha de solicitud es obligatoria");
        } else if (request.getFechaSolicitud().isAfter(LocalDate.now())) {
            errors.rejectValue("fechaSolicitud", "formato", "La fecha no puede ser futura");
        }

        // descripcion opcional pero con límite razonable
        if (request.getDescripcion() != null && request.getDescripcion().length() > 500) {
            errors.rejectValue("descripcion", "longitud", "La descripción no puede superar 500 caracteres");
        }

        // estado obligatorio (aunque tenga default en BD)
        if (request.getEstado() == null) {
            errors.rejectValue("estado", "obligatorio", "El estado es obligatorio");
        } else {
            boolean valido = false;
            for (Estado e : Estado.values()) {
                if (e.equals(request.getEstado())) {
                    valido = true;
                    break;
                }
            }
            if (!valido) {
                errors.rejectValue("estado", "formato", "Estado no válido");
            }
        }
    }
}
