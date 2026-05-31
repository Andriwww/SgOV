package es.uji.ei1027.clubesportiu.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable; // <- Corregido el import roto
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

import es.uji.ei1027.clubesportiu.dao.APRequestDao;
import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.dao.CandidatoDao;
import es.uji.ei1027.clubesportiu.dao.MensajeChatDao;
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.Estado;
import es.uji.ei1027.clubesportiu.model.MensajeChat;
import es.uji.ei1027.clubesportiu.model.TecnicoOVI;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;

@Controller
@RequestMapping("/APRequest")
public class APRequestController {

    private APRequestDao apRequestDao;
    private AsistentePersonalDao asistentePersonalDao;
    private CandidatoDao candidatoDao; 
    @Autowired
    private MensajeChatDao mensajeChatDao;

    @Autowired
    public void setAPRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    @Autowired
    public void setAsistentePersonalDao(AsistentePersonalDao asistentePersonalDao) {
        this.asistentePersonalDao = asistentePersonalDao;
    }

    @Autowired
    public void setCandidatoDao(CandidatoDao candidatoDao) {
        this.candidatoDao = candidatoDao;
    }

    // LISTAR SOLICITUDES
    @GetMapping("/list")
    public String listAPRequests(Model model, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico != null) {
            model.addAttribute("rol", "TECNICO");
            model.addAttribute("requests", apRequestDao.getAPRequests());
            return "APRequest/list";
        } else if (session.getAttribute("usuarioLogueado") != null) {
            UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
            model.addAttribute("rol", "USUARIO");
            model.addAttribute("requests", apRequestDao.getAPRequestsByUsuario(usuario.getIdUsuario()));
            return "APRequest/list";
        }
        return "redirect:/TecnicoOVI/login";
    }

    // CREAR NUEVA SOLICITUD
    @GetMapping("/add")
    public String addAPRequest(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/UsuarioOVI/login";
        }
        model.addAttribute("apRequest", new APRequest());
        return "APRequest/add";
    }

    @PostMapping("/add")
    public String addAPRequestSubmit(@ModelAttribute("apRequest") APRequest apRequest, 
                                     BindingResult bindingResult, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }
        if (bindingResult.hasErrors()) {
            return "APRequest/add";
        }
        apRequest.setIdUsuario(usuario.getIdUsuario());
        apRequest.setEstado(Estado.pendiente);
        apRequestDao.addAPRequest(apRequest);
        return "redirect:/APRequest/list";
    }

    // ELIMINAR SOLICITUD
    @GetMapping("/delete/{id}")
    public String processDelete(@PathVariable int id) {
        apRequestDao.deleteAPRequest(id);
        return "redirect:/APRequest/list";
    }

    // ASIGNAR ASISTENTES (VISTA TÉCNICO)
    @GetMapping("/assign/{id}")
    public String asignarAsistentes(@PathVariable("id") int id, Model model, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        // 1. Buscamos la solicitud. Si es nula, redirigimos para EVITAR el error 500 en Thymeleaf
        APRequest request = apRequestDao.getAPRequest(id);
        if (request == null) {
            return "redirect:/APRequest/list";
        }

        // 2. Traemos todos los asistentes
        List<AsistentePersonal> todosAsistentes = asistentePersonalDao.getAsistentesPersonales();
        if (todosAsistentes == null) todosAsistentes = new ArrayList<>();

        // 3. Traemos los IDs (Para la comprobación ids.contains() de los botones)
        List<Integer> idsAsignados = candidatoDao.getIdsCandidatosPorSolicitud(id);
        if (idsAsignados == null) idsAsignados = new ArrayList<>();

        // 4. CRÍTICO: Traemos los objetos completos de los candidatos (Por si la vista los pinta en una tabla)
        List<AsistentePersonal> candidatos = candidatoDao.getAsistentesCandidatos(id);
        if (candidatos == null) candidatos = new ArrayList<>();

        // Pasamos TODO al modelo de forma segura
        model.addAttribute("request", request);
        model.addAttribute("asistentes", todosAsistentes);
        model.addAttribute("ids", idsAsignados); 
        model.addAttribute("candidatos", candidatos); // ¡Esto era lo que probablemente faltaba!
        model.addAttribute("tecnico", tecnico); 

        return "APRequest/assign";
    }

    // AÑADIR CANDIDATO
    @GetMapping("/candidatos/add/{idSolicitud}/{idAsistente}")
    public String addCandidato(@PathVariable int idSolicitud, @PathVariable int idAsistente, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        candidatoDao.addCandidato(idSolicitud, idAsistente);

        return "redirect:/APRequest/assign/" + idSolicitud;
    }

    // ENVIAR PROPUESTA
    @GetMapping("/candidatos/enviar/{idSolicitud}")
    public String enviarCandidatos(@PathVariable int idSolicitud, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        return "APRequest/propuestaEnviada";
    }

    // ELIMINAR CANDIDATO
    @GetMapping("/candidatos/delete/{idSolicitud}/{idAsistente}")
    public String deleteCandidato(@PathVariable int idSolicitud, @PathVariable int idAsistente, HttpSession session) {
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        candidatoDao.deleteCandidato(idSolicitud, idAsistente);

        return "redirect:/APRequest/assign/" + idSolicitud;
    }

    // VER CANDIDATOS (VISTA USUARIO OVI)
    @GetMapping("/usuario/candidatos/{idSolicitud}")
    public String verCandidatosUsuario(@PathVariable int idSolicitud, Model model, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        List<AsistentePersonal> candidatos = candidatoDao.getAsistentesCandidatos(idSolicitud);

        model.addAttribute("candidatos", candidatos);
        model.addAttribute("idSolicitud", idSolicitud);

        return "UsuarioOVI/candidatos"; 
    }

    // SELECCIONAR ASISTENTE DEFINITIVO
    @GetMapping("/seleccionar/{idSolicitud}")
    public String seleccionarAsistente(@PathVariable int idSolicitud, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        return "redirect:/APRequest/list";
    }

// VER LISTA DE CANDIDATOS (Corrección de visibilidad de botones por Rol)
    @GetMapping("/candidatos/{idSolicitud}")
    public String verListaCandidatos(@PathVariable("idSolicitud") int idSolicitud, Model model, HttpSession session) {
        
        // 1. Obtenemos los asistentes asignados directamente desde la base de datos
        List<AsistentePersonal> candidatos = candidatoDao.getAsistentesCandidatos(idSolicitud);
        if (candidatos == null) {
            candidatos = new ArrayList<>();
        }

        // 2. Pasamos los datos que van a necesitar las vistas
        model.addAttribute("candidatos", candidatos);
        model.addAttribute("idSolicitud", idSolicitud);
        
        // Pasamos la solicitud entera por si la vista la requiere para sacar el idRequest u otros datos
        APRequest request = apRequestDao.getAPRequest(idSolicitud);
        model.addAttribute("request", request);

        // 3. COMPROBACIÓN Y ASIGNACIÓN DE ROLES (¡Esto arregla los botones ocultos!)
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico != null) {
            model.addAttribute("tecnico", tecnico);
            model.addAttribute("rol", "TECNICO"); // <- ¡CRÍTICO! Activa las opciones del Técnico en candidatos.html
            return "APRequest/candidatos"; 
        }

        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("rol", "USUARIO"); // <- ¡CRÍTICO! Activa las opciones del Usuario en candidatos.html
            return "UsuarioOVI/candidatos";
        }

        // Si no hay sesión válida, redirigimos al login
        return "redirect:/TecnicoOVI/login";
    }

    // VER DETALLE DE CHAT (SÓLO LECTURA)
    @GetMapping("/chat/tecnico/ver/{idChat}")
    public String verDetalleChatTecnico(@PathVariable("idChat") int idChat, Model model, HttpSession session) {
        // 1. Validar sesión del técnico
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        // 2. Obtener el historial de mensajes usando MensajeChat
        List<MensajeChat> mensajes = mensajeChatDao.getMensajesPorChat(idChat);

        // 3. Pasar los datos imprescindibles a la vista
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("idChat", idChat);

        return "APRequest/verChatTecnico"; 
    }

    // MOSTRAR FORMULARIO DE EDICIÓN (GET)
    @GetMapping("/update/{id}")
    public String updateAPRequest(@PathVariable("id") int id, Model model, HttpSession session) {
        // Solo el técnico debería poder cambiar los estados manualmente
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        APRequest request = apRequestDao.getAPRequest(id);
        if (request == null) {
            return "redirect:/APRequest/list";
        }

        model.addAttribute("apRequest", request);
        // Pasamos todos los valores del Enum Estado para que el HTML pueda montar un <select>
        model.addAttribute("estados", Estado.values()); 
        
        return "APRequest/update"; // Esto llamará a tu archivo update.html
    }

    // PROCESAR FORMULARIO DE EDICIÓN (POST)
    @PostMapping("/update")
    public String processUpdateSubmit(@ModelAttribute("apRequest") APRequest apRequest, 
                                      BindingResult bindingResult, HttpSession session) {
                                          
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        if (bindingResult.hasErrors()) {
            return "APRequest/update";
        }
        
        // Llamamos al DAO para que guarde el nuevo estado
        apRequestDao.updateAPRequest(apRequest);
        
        return "redirect:/APRequest/list";
    }
}