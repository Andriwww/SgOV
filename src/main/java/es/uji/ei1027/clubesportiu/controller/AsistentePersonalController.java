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

    // LIST
    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("asistentes", asistentePersonalDao.getAsistentesPersonales());
        return "AsistentePersonal/list";
    }

    // ADD FORM
    @RequestMapping("/login")
    public String login() {
        return "AsistentePersonal/login";
    }

    // ADD SUBMIT
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String addSubmit(@ModelAttribute("asistente") AsistentePersonal asistente,
                            BindingResult result) {

        AsistentePersonalValidator validator = new AsistentePersonalValidator(asistentePersonalDao);
        validator.validate(asistente, result);

        if (result.hasErrors()) {
            return "AsistentePersonal/login";
        }

        asistentePersonalDao.addAsistentePersonal(asistente);
        return "redirect:list";
    }

    // UPDATE FORM
    @RequestMapping("/update/{idAsistente}")
    public String editForm(Model model, @PathVariable int idAsistente) {
        model.addAttribute("asistente",
                asistentePersonalDao.getAsistentePersonal(idAsistente));
        return "AsistentePersonal/update";
    }

    // UPDATE SUBMIT
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String editSubmit(@ModelAttribute("asistente") AsistentePersonal asistente,
                             BindingResult result) {

        AsistentePersonalValidator validator = new AsistentePersonalValidator(asistentePersonalDao);
        validator.validate(asistente, result);

        if (result.hasErrors()) {
            return "AsistentePersonal/update";
        }

        asistentePersonalDao.updateAsistentePersonal(asistente);
        return "redirect:list";
    }


    // DELETE
    @RequestMapping("/delete/{idAsistente}")
    public String delete(@PathVariable int idAsistente) {
        asistentePersonalDao.deleteAsistentePersonal(idAsistente);
        return "redirect:/AsistentePersonal/list";
    }
    
}
