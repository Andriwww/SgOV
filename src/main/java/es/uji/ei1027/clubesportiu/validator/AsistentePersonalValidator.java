package es.uji.ei1027.clubesportiu.validator;

import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AsistentePersonalValidator implements Validator{
    @Override
    public boolean supports(Class<?> cls) {
        return AsistentePersonal.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        AsistentePersonal asistente = (AsistentePersonal) obj;

        // nombre obligatorio
        if (asistente.getNombre() == null || asistente.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "obligatorio", "El nombre es obligatorio");
        }

        // apellidos obligatorios
        if (asistente.getApellidos() == null || asistente.getApellidos().trim().isEmpty()) {
            errors.rejectValue("apellidos", "obligatorio", "Los apellidos son obligatorios");
        }

        // email obligatorio + formato simple
        if (asistente.getEmail() == null || asistente.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio");
        } else if (!asistente.getEmail().contains("@")) {
            errors.rejectValue("email", "formato", "El email no es válido");
        }

        // teléfono opcional pero si existe → 9 dígitos
        if (asistente.getTelefono() != null && !asistente.getTelefono().trim().isEmpty()) {
            if (!asistente.getTelefono().matches("^[0-9]{9}$")) {
                errors.rejectValue("telefono", "formato", "El teléfono debe tener 9 dígitos");
            }
        }

        // disponibilidad 
        if (asistente.getDisponibilidad() != null &&
            asistente.getDisponibilidad().length() > 200) {
            errors.rejectValue("disponibilidad", "longitud",
                    "La disponibilidad no puede superar 200 caracteres");
        }

        // estadoAceptado y activo no se validan (son booleanos controlados)
    }
}
