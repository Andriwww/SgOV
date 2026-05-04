package es.uji.ei1027.clubesportiu.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import es.uji.ei1027.clubesportiu.model.ComunicacionUsuarioOVIPAP;

public class ComunicacionUsuarioOVIPAPValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return ComunicacionUsuarioOVIPAP.class.equals(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ComunicacionUsuarioOVIPAP comunicacion = (ComunicacionUsuarioOVIPAP) obj;

        if (comunicacion.getMensaje() == null || comunicacion.getMensaje().trim().equals(""))
            errors.rejectValue("mensaje", "obligatorio", "El mensaje es obligatorio");

        if (comunicacion.getEmisor() == null || comunicacion.getEmisor().trim().equals(""))
            errors.rejectValue("emisor", "obligatorio", "El emisor es obligatorio");

        if (comunicacion.getReceptor() == null || comunicacion.getReceptor().trim().equals(""))
            errors.rejectValue("receptor", "obligatorio", "El receptor es obligatorio");
            
        if (comunicacion.getFecha() == null)
            errors.rejectValue("fecha", "obligatorio", "La fecha es obligatoria");
    }
}