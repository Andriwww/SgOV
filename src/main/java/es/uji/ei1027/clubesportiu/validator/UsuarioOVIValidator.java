package es.uji.ei1027.clubesportiu.validator;

import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UsuarioOVIValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return UsuarioOVI.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        UsuarioOVI usuario = (UsuarioOVI) obj;

        if (usuario.getNombre() == null || usuario.getNombre().trim().equals(""))
            errors.rejectValue("nombre", "obligatorio", "El nombre es obligatorio");

        if (usuario.getApellidos() == null || usuario.getApellidos().trim().equals(""))
            errors.rejectValue("apellidos", "obligatorio", "Los apellidos son obligatorios");

        if (usuario.getEmail() == null || usuario.getEmail().trim().equals(""))
            errors.rejectValue("email", "obligatorio", "El email es obligatorio");
        else if (!usuario.getEmail().contains("@"))
            errors.rejectValue("email", "formato", "El email no es válido");

        if (usuario.getTelefono() != null && !usuario.getTelefono().trim().equals(""))
            if (!usuario.getTelefono().matches("^[0-9]{9}$"))
                errors.rejectValue("telefono", "formato", "El teléfono debe tener 9 dígitos");
    }
}