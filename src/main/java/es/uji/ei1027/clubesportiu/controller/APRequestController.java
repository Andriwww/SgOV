package es.uji.ei1027.clubesportiu.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.clubesportiu.dao.APRequestDao;
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.Estado;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import es.uji.ei1027.clubesportiu.validator.APRequestValidator;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/APRequest")
public class APRequestController {
    private APRequestDao apRequestDao;

    @Autowired
    public void setApRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    // LISTAR
    @RequestMapping("/list")
    public String list(Model model, HttpSession session) {
        UsuarioOVI usuarioLogueado = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }
        model.addAttribute("requests", apRequestDao.getAPRequestsByUsuario(usuarioLogueado.getIdUsuario()));
        return "APRequest/list";
    }

    // FORMULARIO AÑADIR
    @RequestMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("apRequest", new APRequest());
        return "APRequest/add";
    }

    // GUARDAR
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@ModelAttribute("apRequest") APRequest apRequest, BindingResult bindingResult, HttpSession session) {

        UsuarioOVI usuarioLogueado = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado != null) {
            apRequest.setIdUsuario(usuarioLogueado.getIdUsuario());
        }

        apRequest.setEstado(Estado.Pendiente);

        APRequestValidator validator = new APRequestValidator();
        validator.validate(apRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            return "APRequest/add";
        }

        apRequestDao.addAPRequest(apRequest);
        
        return "redirect:/UsuarioOVI/dashboard";
    }

    // FORMULARIO EDITAR
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("request", apRequestDao.getAPRequest(id));
        return "APRequest/update";
    }

    // ACTUALIZAR
    @PostMapping("/edit")
    public String editSubmit(@ModelAttribute APRequest request) {
        apRequestDao.updateAPRequest(request);
        return "redirect:/APRequest/list";
    }

    // BORRAR
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        apRequestDao.deleteAPRequest(id);
        return "redirect:/APRequest/list";
    }
}
