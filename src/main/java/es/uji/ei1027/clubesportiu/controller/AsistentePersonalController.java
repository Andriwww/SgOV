package es.uji.ei1027.clubesportiu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.validator.AsistentePersonalValidator;

@Controller
@RequestMapping("/AsistentePersonal")
public class AsistentePersonalController {

    private AsistentePersonalDao asistentePersonalDao;

    @Autowired
    public void setAsistentePersonalDao(AsistentePersonalDao dao) {
        this.asistentePersonalDao = dao;
    }

    // LISTAR ASISTENTES
    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("asistentes", asistentePersonalDao.getAsistentesPersonales());
        return "AsistentePersonal/list";
    }

    // MOSTRAR FORMULARIO DE ALTA / SOLICITUD (GET)
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model model) {
        model.addAttribute("asistente", new AsistentePersonal());
        return "AsistentePersonal/register"; // Renderiza el archivo register.html
    }

    // PROCESAR FORMULARIO DE ALTA / SOLICITUD (POST)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerSubmit(@ModelAttribute("asistente") AsistentePersonal asistente,
                                BindingResult result) {

        AsistentePersonalValidator validator = new AsistentePersonalValidator(asistentePersonalDao);
        validator.validate(asistente, result);

        if (result.hasErrors()) {
            return "AsistentePersonal/register"; // Si falla, recarga register.html mostrando los mensajes de error
        }

        asistentePersonalDao.addAsistentePersonal(asistente);
        return "redirect:/AsistentePersonal/esperaValidacion/" + asistente.getIdAsistente(); // Redirige a la página de espera de validación
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("asistente", new AsistentePersonal());
        return "AsistentePersonal/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginSubmit(@ModelAttribute("asistente") AsistentePersonal asistente,
                              BindingResult result) {
        AsistentePersonal asistenteBD = asistentePersonalDao.getAsistentePersonalByEmail(asistente.getEmail());

        if (asistenteBD == null || !asistenteBD.getContraseña().equals(asistente.getContraseña())) {
            result.rejectValue("email", "invalid", "Correo electrónico o contraseña incorrectos");
            return "AsistentePersonal/login";
        }

        return "redirect:/AsistentePersonal/esperaValidacion/" + asistenteBD.getIdAsistente();
    }

     // MOSTRAR PERFIL DEL ASISTENTE
     @RequestMapping("/perfil/{idAsistente}")
     public String perfil(Model model, @PathVariable int idAsistente) {
         model.addAttribute("asistente", asistentePersonalDao.getAsistentePersonal(idAsistente));
         return "AsistentePersonal/perfil"; // Renderiza el archivo perfil.html
     }

    // MOSTRAR FORMULARIO DE EDICIÓN DE PERFIL (GET)
    @RequestMapping("/update/{idAsistente}")
    public String editForm(Model model, @PathVariable int idAsistente) {
        model.addAttribute("asistente", asistentePersonalDao.getAsistentePersonal(idAsistente));
        return "AsistentePersonal/update"; // Renderiza el archivo update.html
    }

    // PROCESAR FORMULARIO DE EDICIÓN DE PERFIL (POST)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String editSubmit(@ModelAttribute("asistente") AsistentePersonal asistente,
                             BindingResult result) {

        AsistentePersonalValidator validator = new AsistentePersonalValidator(asistentePersonalDao);
        validator.validate(asistente, result);

        if (result.hasErrors()) {
            return "AsistentePersonal/update"; // Si falla la edición, recarga update.html reteniendo los campos erróneos
        }

        asistentePersonalDao.updateAsistentePersonal(asistente);
        return "redirect:main";
    }

    // ELIMINAR ASISTENTE
    @RequestMapping("/delete/{idAsistente}")
    public String delete(@PathVariable int idAsistente) {
        asistentePersonalDao.deleteAsistentePersonal(idAsistente);
        return "redirect:/main";
    }

    @RequestMapping(value = "/esperaValidacion/{idAsistente}", method = RequestMethod.GET)
    public String esperaValidacion(@PathVariable int idAsistente, Model model) {
        AsistentePersonal asistente = asistentePersonalDao.getAsistentePersonal(idAsistente);
        if (asistente == null) {
            return "redirect:/AsistentePersonal/register";
        }
        if (asistente.isEstadoAceptado()) {
            return "redirect:/main";
        }

        model.addAttribute("asistente", asistente);
        return "AsistentePersonal/esperaValidacion";
    }
}