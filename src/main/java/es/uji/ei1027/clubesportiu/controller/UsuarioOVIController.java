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

import es.uji.ei1027.clubesportiu.dao.UsuarioOVIDao;
import es.uji.ei1027.clubesportiu.model.UserDetails;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import es.uji.ei1027.clubesportiu.validator.UsuarioOVIValidator;
import jakarta.servlet.http.HttpSession;


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

    @RequestMapping(value = "/register")
    public String addForm(Model model) {
        model.addAttribute("usuario", new UsuarioOVI());
        return "UsuarioOVI/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String addSubmit(@ModelAttribute("usuario") UsuarioOVI usuario, BindingResult bindingResult) {
        UsuarioOVIValidator validator = new UsuarioOVIValidator(usuarioOVIDao);
        validator.validate(usuario, bindingResult);
        if (bindingResult.hasErrors()) {
            return "UsuarioOVI/register";
        }
        usuario.setEstadoAceptado(false);
        usuarioOVIDao.addUsuarioOVI(usuario);
        return "redirect:/";
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

    @GetMapping("/confirm-delete/{id}")
    public String confirmDelete(@PathVariable int id, Model model) {
        model.addAttribute("usuario", usuarioOVIDao.getUsuarioOVI(id));
        return "UsuarioOVI/delete";
    }
    

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        usuarioOVIDao.deleteUsuarioOVI(id);
        return "redirect:/UsuarioOVI/list";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserDetails());
        return "UsuarioOVI/login"; 
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String checkLogin(@ModelAttribute("user") UserDetails userDetails, 
                             BindingResult bindingResult, HttpSession session) {
        
        if (bindingResult.hasErrors()) {
            return "UsuarioOVI/login";
        }

        UsuarioOVI usuario = usuarioOVIDao.loadUserByUsername(userDetails.getUsuario(), userDetails.getPassword());

        if (usuario == null) {
            session.setAttribute("error", "Usuario o contraseña incorrectos");
            return "redirect:/UsuarioOVI/login";
        }

        if (!usuario.isEstadoAceptado()) {
            return "UsuarioOVI/pending";
        }

        session.removeAttribute("error");
        session.setAttribute("usuarioLogueado", usuario);
        return "redirect:/UsuarioOVI/dashboard";
    }

    @RequestMapping("/pending")
    public String pending() {
        return "UsuarioOVI/pending";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }
        model.addAttribute("nombreUsuario", usuario.getNombre());
        return "UsuarioOVI/dashboard";
    }
}