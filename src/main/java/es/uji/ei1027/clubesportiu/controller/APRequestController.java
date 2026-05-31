package es.uji.ei1027.clubesportiu.controller;


import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

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
import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.Estado;
import es.uji.ei1027.clubesportiu.model.TecnicoOVI;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import es.uji.ei1027.clubesportiu.validator.APRequestValidator;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/APRequest")
public class APRequestController {
    private APRequestDao apRequestDao;
    
    private AsistentePersonalDao asistentePersonalDao;
    private Map<Integer, List<AsistentePersonal>> candidatosPorSolicitud = new HashMap<>();
    private Map<Integer, List<AsistentePersonal>> candidatosEnviados = new HashMap<>();

    @Autowired
    public void setApRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    @Autowired
    public void setAsistentePersonalDao(AsistentePersonalDao asistentePersonalDao) {
        this.asistentePersonalDao = asistentePersonalDao;
    }

    // LISTAR
    @RequestMapping("/list")
    public String list(Model model, HttpSession session) {

        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");

        model.addAttribute("candidatosEnviados", candidatosEnviados); 

        if (tecnico != null) {
            model.addAttribute("requests", apRequestDao.getAPRequests());
            model.addAttribute("rol", "TECNICO");
            return "APRequest/list";
        }

        if (usuario != null) {
            model.addAttribute("requests",
                    apRequestDao.getAPRequestsByUsuario(usuario.getIdUsuario()));
            model.addAttribute("rol", "USUARIO");
            return "APRequest/list";
        }

        return "redirect:/";
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

        apRequest.setEstado(Estado.pendiente);

        APRequestValidator validator = new APRequestValidator();
        validator.validate(apRequest, bindingResult);

        if (bindingResult.hasErrors()) {
            return "APRequest/add";
        }

        apRequestDao.addAPRequest(apRequest);
        
        return "redirect:/APRequest/list";
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

    // MOSTRAR EL FORMULARIO DE EDICIÓN
    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editRequest(Model model, @PathVariable int id, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/";
        }

        APRequest request = apRequestDao.getAPRequest(id);
        
        model.addAttribute("aprequest", request);
        
        return "APRequest/update";
    }

    // GUARDAR LOS CAMBIOS
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("aprequest") APRequest request, 
                                    BindingResult bindingResult, HttpSession session) {

        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "APRequest/update";
        }

        apRequestDao.updateEstadoAPRequest(request);
        
        return "redirect:/APRequest/list";
    }

    // BORRAR
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        apRequestDao.deleteAPRequest(id);
        return "redirect:/APRequest/list";
    }

    // LISTAR ASISTENTES
    @GetMapping("/assign/{id}")
    public String assignAssistants(@PathVariable int id, Model model) {

        APRequest request = apRequestDao.getAPRequest(id);

        List<AsistentePersonal> asistentes =
                asistentePersonalDao.getAsistentesPersonales();

        List<AsistentePersonal> candidatos =
                candidatosPorSolicitud.getOrDefault(id, new ArrayList<>());

        model.addAttribute("request", request);
        model.addAttribute("asistentes", asistentes);
        model.addAttribute("candidatos", candidatos);

        return "APRequest/assign";
    }

    // AÑADIR CANDIDATO
    @GetMapping("/candidatos/add/{idSolicitud}/{idAsistente}")
    public String addCandidato(@PathVariable int idSolicitud,
                            @PathVariable int idAsistente) {

        AsistentePersonal asistente =
                asistentePersonalDao.getAsistentePersonal(idAsistente);

        candidatosPorSolicitud
            .computeIfAbsent(idSolicitud, k -> new ArrayList<>())
            .add(asistente);

        return "redirect:/APRequest/assign/" + idSolicitud;
    }

    // VER CANDIDATOS
    @GetMapping("/candidatos/{idSolicitud}")
    public String verCandidatos(@PathVariable int idSolicitud, Model model) {

        List<AsistentePersonal> candidatos =
                candidatosPorSolicitud.getOrDefault(idSolicitud, new ArrayList<>());

        model.addAttribute("candidatos", candidatos);
        model.addAttribute("idSolicitud", idSolicitud);
        model.addAttribute("rol", "TECNICO");

        return "APRequest/candidatos";
    }

    // ENVIAR LISTA CANDIDATOS
    @GetMapping("/candidatos/enviar/{idSolicitud}")
    public String enviarCandidatos(@PathVariable int idSolicitud) {

        List<AsistentePersonal> candidatos =
                candidatosPorSolicitud.get(idSolicitud);

        if (candidatos != null) {
            candidatosEnviados.put(idSolicitud,
                    new ArrayList<>(candidatos));
        }

        return "redirect:/APRequest/list";
    }

    // MOSTRAR CANDIDATOS A USUARIO
    @GetMapping("/candidatos/usuario/{idSolicitud}")
    public String verCandidatosUsuario(@PathVariable int idSolicitud, Model model) {

        List<AsistentePersonal> candidatos =
                candidatosEnviados.getOrDefault(idSolicitud, new ArrayList<>());

        model.addAttribute("candidatos", candidatos);
        model.addAttribute("idSolicitud", idSolicitud);
        model.addAttribute("rol", "USUARIO");

        return "APRequest/candidatos";
    }

    // ELIMINAR CANDIDATO
    @GetMapping("/candidatos/delete/{idSolicitud}/{idAsistente}")
    public String deleteCandidato(@PathVariable int idSolicitud,
                                @PathVariable int idAsistente) {

        List<AsistentePersonal> lista =
                candidatosPorSolicitud.get(idSolicitud);

        if (lista != null) {
            lista.removeIf(a -> a.getIdAsistente() == idAsistente);
        }

        return "redirect:/APRequest/candidatos/" + idSolicitud;
    }

    @GetMapping("/usuario/candidatos/{idSolicitud}")
    public String verCandidatosUsuario(@PathVariable int idSolicitud, Model model, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        // Recuperamos la lista de candidatos asignados a esta solicitud
        List<AsistentePersonal> candidatos =
                candidatosPorSolicitud.getOrDefault(idSolicitud, new ArrayList<>());

        model.addAttribute("candidatos", candidatos);
        model.addAttribute("idSolicitud", idSolicitud);

        // Devolvemos la nueva vista que vamos a crear en la carpeta UsuarioOVI
        return "UsuarioOVI/candidatos"; 
    }

    // SELECCIONAR ASISTENTE DEFINITIVO (MÉTODO PREPARATORIO)
    @GetMapping("/seleccionar/{idSolicitud}")
    public String seleccionarAsistente(@PathVariable int idSolicitud, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/UsuarioOVI/login";

        // Aquí irá la lógica para guardar la selección final del usuario en la base de datos
        // Por ejemplo, enlazar el ID del Asistente elegido a la solicitud o cambiar el estado.
        // De momento redirigimos a la lista de candidatos para que elijan uno
        
        return "redirect:/APRequest/usuario/candidatos/" + idSolicitud;
    }
}
