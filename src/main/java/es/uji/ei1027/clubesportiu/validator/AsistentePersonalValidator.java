package es.uji.ei1027.clubesportiu.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;

public class AsistentePersonalValidator implements Validator {

    private final AsistentePersonalDao asistentePersonalDao;

    public AsistentePersonalValidator(AsistentePersonalDao asistentePersonalDao) {
        this.asistentePersonalDao = asistentePersonalDao; 
    }

    @Override
    public boolean supports(Class<?> cls) {
        return AsistentePersonal.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        AsistentePersonal asistente = (AsistentePersonal) obj;

        // Validar nombre
        if (asistente.getNombre() == null || asistente.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "obligatorio", "El nombre es obligatorio");
        }

        // Validar apellidos
        if (asistente.getApellidos() == null || asistente.getApellidos().trim().isEmpty()) {
            errors.rejectValue("apellidos", "obligatorio", "Los apellidos son obligatorios");
        }

        // Validar email (presencia, formato arroba y unicidad en base de datos)
        if (asistente.getEmail() == null || asistente.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "obligatorio", "El email es obligatorio");
        } else if (!asistente.getEmail().contains("@")) {
            errors.rejectValue("email", "formato", "El email no contiene un formato válido");
        } else if (asistentePersonalDao.existeEmail(asistente.getEmail(), asistente.getIdAsistente())) {
            errors.rejectValue("email", "repetido", "Este correo electrónico ya se encuentra registrado");
        }

        // VALIDACIONES EXCLUSIVAS DE REGISTRO DE NUEVOS USUARIOS
        if (asistente.getIdAsistente() == 0) {
            // Contraseña obligatoria en alta
            if (asistente.getContraseña() == null || asistente.getContraseña().trim().isEmpty()) {
                errors.rejectValue("contraseña", "obligatorio", "La contraseña es obligatoria");
            }
            // Consentimiento legal obligatorio en alta
            if (!asistente.isConsentimientoRGBD()) {
                errors.rejectValue("consentimientoRGBD", "aceptacion", "Debes aceptar el consentimiento RGPD para continuar");
            }
        }

        // Validar teléfono opcional (Si se escribe, debe cumplir la expresión regular de 9 dígitos)
        if (asistente.getTelefono() != null && !asistente.getTelefono().trim().isEmpty()) {
            if (!asistente.getTelefono().matches("^[0-9]{9}$")) {
                errors.rejectValue("telefono", "formato", "El teléfono debe estar compuesto por exactamente 9 dígitos numéricos");
            }
        }

        // Validar tamaño máximo del campo disponibilidad
        if (asistente.getDisponibilidad() != null && asistente.getDisponibilidad().length() > 200) {
            errors.rejectValue("disponibilidad", "longitud", "La descripción de la disponibilidad no puede superar los 200 caracteres");
        }
    }
}