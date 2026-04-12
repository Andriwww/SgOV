package es.uji.ei1027.clubesportiu.controller;

import es.uji.ei1027.clubesportiu.dao.UsuarioOVIDao;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import es.uji.ei1027.clubesportiu.validator.UsuarioOVIValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/UsuarioOVI")
public class UsuarioOVIController {

    private UsuarioOVIDao usuarioOVIDao;

    @Autowired
    public void setUsuarioOVIDao(UsuarioOVIDao usuarioOVIDao) {
        this.usuarioOVIDao = usuarioOVIDao;
    }

    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("usuarios", usuarioOVIDao.getUsuariosOVI());
        return "UsuarioOVI/list";
    }

    @RequestMapping(value = "/add")
    public String addForm(Model model) {
        model.addAttribute("usuario", new UsuarioOVI());
        return "UsuarioOVI/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@ModelAttribute("usuario") UsuarioOVI usuario, BindingResult bindingResult) {
        UsuarioOVIValidator validator = new UsuarioOVIValidator();
        validator.validate(usuario, bindingResult);
        if (bindingResult.hasErrors())
            return "UsuarioOVI/add";
        usuarioOVIDao.addUsuarioOVI(usuario);
        return "redirect:list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("usuario", usuarioOVIDao.getUsuarioOVI(id));
        return "UsuarioOVI/edit";
    }

    @PostMapping("/edit")
    public String editSubmit(@ModelAttribute UsuarioOVI usuario) {
        usuarioOVIDao.updateUsuarioOVI(usuario);
        return "redirect:/UsuarioOVI/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        usuarioOVIDao.deleteUsuarioOVI(id);
        return "redirect:/UsuarioOVI/list";
    }
}