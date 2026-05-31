package es.uji.ei1027.clubesportiu.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.uji.ei1027.clubesportiu.dao.APRequestDao;
import es.uji.ei1027.clubesportiu.dao.ChatDao;
import es.uji.ei1027.clubesportiu.dao.MensajeChatDao;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.ChatSession;
import es.uji.ei1027.clubesportiu.model.MensajeChat;
import es.uji.ei1027.clubesportiu.model.TecnicoOVI;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private ChatDao chatDao;
    private APRequestDao apRequestDao; 

    @Autowired
    public void setChatDao(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    @Autowired
    public void setApRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }

    // ==========================================
    //  SECCIÓN DEL USUARIO OVI
    // ==========================================

    // SALA DEL USUARIO
    @GetMapping("/usuario/sala/{idChat}")
    public String salaUsuario(@PathVariable int idChat, Model model, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/UsuarioOVI/login";

        List<ChatSession> chats = chatDao.getChatsPorUsuario(usuario.getIdUsuario());
        model.addAttribute("chats", chats);

        if (idChat > 0) {
            ChatSession chatActual = chatDao.getChat(idChat);
            // Validación de seguridad básica
            if (chatActual != null && chatActual.getIdUsuario() == usuario.getIdUsuario()) {
                model.addAttribute("chatActual", chatActual);
                model.addAttribute("mensajes", chatDao.getMensajesDelChat(idChat));
            } else {
                return "redirect:/chat/usuario/sala/0";
            }
        } else {
            // Si es 0, enviamos un objeto vacío para evitar errores de Thymeleaf
            model.addAttribute("chatActual", new ChatSession());
            model.addAttribute("mensajes", new ArrayList<MensajeChat>());
        }

        return "UsuarioOVI/sala";
    }

    @PostMapping("/usuario/enviar/{idChat}")
    public String usuarioEnviarMensaje(@PathVariable int idChat, @RequestParam String contenido, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        if (idChat > 0 && contenido != null && !contenido.trim().isEmpty()) {
            chatDao.guardarMensaje(idChat, "USUARIO", contenido.trim());
        }

        return "redirect:/chat/usuario/sala/" + idChat;
    }


    // ==========================================
    //  SECCIÓN DEL ASISTENTE PERSONAL
    // ==========================================

    // SALA DEL ASISTENTE
    @GetMapping("/asistente/sala/{idChat}")
    public String salaAsistente(@PathVariable int idChat, Model model, HttpSession session) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) return "redirect:/AsistentePersonal/login";

        List<ChatSession> chats = chatDao.getChatsPorAsistente(asistente.getIdAsistente());
        model.addAttribute("chats", chats);

        if (idChat > 0) {
            ChatSession chatActual = chatDao.getChat(idChat);
            if (chatActual != null && chatActual.getIdAsistente() == asistente.getIdAsistente()) {
                model.addAttribute("chatActual", chatActual);
                model.addAttribute("mensajes", chatDao.getMensajesDelChat(idChat));
            } else {
                return "redirect:/chat/asistente/sala/0";
            }
        } else {
            model.addAttribute("chatActual", new ChatSession());
            model.addAttribute("mensajes", new ArrayList<MensajeChat>());
        }

        return "AsistentePersonal/sala";
    }

@PostMapping("/asistente/enviar/{idChat}")
public String asistenteEnviarMensaje(@PathVariable("idChat") int idChat,
                                     @RequestParam("contenido") String contenido,
                                     @RequestParam(value = "idRequest", required = false) Integer idRequest,
                                     HttpSession session) {
    
    // 1. Validar que el asistente esté logueado
    AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
    if (asistente == null) {
        return "redirect:/AsistentePersonal/login";
    }

    int idAsistenteReal = asistente.getIdAsistente();

    // 2. Si es el primer mensaje y la sala aún no se había creado (idChat llega a 0)
    if (idChat == 0 && idRequest != null) {
        es.uji.ei1027.clubesportiu.model.APRequest sol = apRequestDao.getAPRequest(idRequest);
        if (sol != null) {
            chatDao.iniciarChat(sol.getIdUsuario(), idAsistenteReal, idRequest);
            
            // Recuperamos el idChat real autogenerado
            List<ChatSession> todos = chatDao.getTodosLosChats();
            for (ChatSession cs : todos) {
                if (cs.getIdRequest() == idRequest && cs.getIdAsistente() == idAsistenteReal) {
                    idChat = cs.getIdChat();
                    break;
                }
            }
        }
    }

    // 3. Guardar el mensaje enviado por el asistente
    if (idChat > 0 && contenido != null && !contenido.trim().isEmpty()) {
        chatDao.guardarMensaje(idChat, "ASISTENTE", contenido.trim());
    }

    // ✨ EL CAMBIO IMPORTANTE AQUÍ:
    // En lugar de redirigir al listado o salir, volvemos a cargar la misma sala
    return "redirect:/chat/asistente/sala/" + idChat;
}

    @GetMapping("/iniciar/{idAsistente}/{idReq}")
    public String iniciarChatDesdeCandidato(@PathVariable("idAsistente") int idAsistente, 
                                        @PathVariable("idReq") int idRequest, 
                                        HttpSession session) {
        // 1. Validar seguridad del usuario común
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/UsuarioOVI/login";

        // 2. Crear la fila en la tabla 'chatsession' si no existía ya
        chatDao.iniciarChat(usuario.getIdUsuario(), idAsistente, idRequest);

        // 3. Recuperar el idChat real autogenerado por la base de datos
        List<ChatSession> chats = chatDao.getChatsPorUsuario(usuario.getIdUsuario());
        int idChatDestino = 0;
        for (ChatSession cs : chats) {
            if (cs.getIdRequest() == idRequest && cs.getIdAsistente() == idAsistente) {
                idChatDestino = cs.getIdChat();
                break;
            }
        }

        // 4. Redirigir directamente a la vista de la sala con el ID correcto
        return "redirect:/chat/usuario/sala/" + idChatDestino;
    }

    // ==========================================
    //  SECCIÓN DEL TÉCNICO OVI
    // ==========================================

    @GetMapping("/tecnico/lista")
    public String listaChatsTecnico(Model model, HttpSession session) {
        // Control de seguridad: verificamos que haya un técnico logueado en la sesión
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        // Recuperamos todos los hilos de conversación de la plataforma
        List<ChatSession> todosLosChats = chatDao.getTodosLosChats();
        model.addAttribute("chats", todosLosChats);
        
        // Pasamos el rol 'TECNICO' para mantener la coherencia con tus otras vistas
        model.addAttribute("rol", "TECNICO");

        return "TecnicoOVI/listaChats"; // Ubicado en src/main/resources/templates/TecnicoOVI/listaChats.html
    }

// VER DETALLE DE CHAT (VISTA TÉCNICO - SOLO LECTURA)
    @GetMapping("/tecnico/ver/{idChat}") // 💡 CORREGIDO: Quitamos /chat porque ya está definido arriba a nivel de clase @RequestMapping("/chat")
    public String verDetalleChatTecnico(@PathVariable("idChat") int idChat, Model model, HttpSession session) {
        // 1. Validar que es un técnico quien accede
        TecnicoOVI tecnico = (TecnicoOVI) session.getAttribute("tecnicoLogueado");
        if (tecnico == null) {
            return "redirect:/TecnicoOVI/login";
        }

        // 2. Obtener los mensajes usando el modelo MensajeChat a través de chatDao
        List<MensajeChat> mensajes = chatDao.getMensajesPorChat(idChat);
        if (mensajes == null) {
            mensajes = new ArrayList<>();
        }

        // 3. Pasar los datos imprescindibles a la vista
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("idChat", idChat);
        model.addAttribute("mensajes", mensajes);

        // Devuelve la plantilla HTML de solo lectura que creamos previamente
        return "APRequest/verChatTecnico"; 
    }
}