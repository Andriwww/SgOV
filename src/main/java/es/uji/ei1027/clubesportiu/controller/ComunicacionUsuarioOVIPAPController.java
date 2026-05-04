package es.uji.ei1027.clubesportiu.controller;

import es.uji.ei1027.clubesportiu.dao.ComunicacionUsuarioOVIPAPDao;
import es.uji.ei1027.clubesportiu.model.ComunicacionUsuarioOVIPAP;
import es.uji.ei1027.clubesportiu.validator.ComunicacionUsuarioOVIPAPValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ComunicacionUsuarioOVIPAP")
public class ComunicacionUsuarioOVIPAPController {

    private ComunicacionUsuarioOVIPAPDao comunicacionDao;

    @Autowired
    public void setComunicacionUsuarioOVIPAPDao(ComunicacionUsuarioOVIPAPDao comunicacionDao) {
        this.comunicacionDao = comunicacionDao;
    }

    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("comunicaciones", comunicacionDao.getComunicacionesUsuarioOVIPAP());
        return "ComunicacionUsuarioOVIPAP/list";
    }

    @RequestMapping(value = "/add")
    public String addForm(Model model) {
        model.addAttribute("comunicacion", new ComunicacionUsuarioOVIPAP());
        return "ComunicacionUsuarioOVIPAP/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@ModelAttribute("comunicacion") ComunicacionUsuarioOVIPAP comunicacion, BindingResult bindingResult) {
        ComunicacionUsuarioOVIPAPValidator validator = new ComunicacionUsuarioOVIPAPValidator();
        validator.validate(comunicacion, bindingResult);
        if (bindingResult.hasErrors())
            return "ComunicacionUsuarioOVIPAP/add";
        comunicacionDao.addComunicacionUsuarioOVIPAP(comunicacion);
        return "redirect:list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("comunicacion", comunicacionDao.getComunicacionUsuarioOVIPAP(id));
        return "ComunicacionUsuarioOVIPAP/edit";
    }

    @PostMapping("/edit")
    public String editSubmit(@ModelAttribute ComunicacionUsuarioOVIPAP comunicacion, BindingResult bindingResult) {
        ComunicacionUsuarioOVIPAPValidator validator = new ComunicacionUsuarioOVIPAPValidator();
        validator.validate(comunicacion, bindingResult);
        if (bindingResult.hasErrors())
            return "ComunicacionUsuarioOVIPAP/edit";
        
        comunicacionDao.updateComunicacionUsuarioOVIPAP(comunicacion);
        return "redirect:/ComunicacionUsuarioOVIPAP/list";
    }

    @GetMapping("/confirm-delete/{id}")
    public String confirmDelete(@PathVariable int id, Model model) {
        model.addAttribute("comunicacion", comunicacionDao.getComunicacionUsuarioOVIPAP(id));
        return "ComunicacionUsuarioOVIPAP/delete";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        comunicacionDao.deleteComunicacionUsuarioOVIPAP(id);
        return "redirect:/ComunicacionUsuarioOVIPAP/list";
    }
}
