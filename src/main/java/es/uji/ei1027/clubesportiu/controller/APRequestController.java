package es.uji.ei1027.clubesportiu.controller;


import es.uji.ei1027.clubesportiu.dao.APRequestDao;
import es.uji.ei1027.clubesportiu.model.APRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String list(Model model) {
        model.addAttribute("requests", apRequestDao.getAPRequests());
        return "APRequest/list";
    }

    // FORMULARIO AÑADIR
    @RequestMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("request", new APRequest());
        return "APRequest/add";
    }

    // GUARDAR
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@ModelAttribute("request") APRequest request) {
        apRequestDao.addAPRequest(request);
        return "redirect:list";
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
