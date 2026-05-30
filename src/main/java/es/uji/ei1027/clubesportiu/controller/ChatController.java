package es.uji.ei1027.clubesportiu.controller;

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
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.ChatSession;
import es.uji.ei1027.clubesportiu.model.MensajeChat;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private ChatDao chatDao;
    private APRequestDao apRequestDao; // Asegúrate de tener este DAO para obtener detalles de las solicitudes

    @Autowired
    public void setChatDao(ChatDao chatDao) {
        this.chatDao = chatDao;
    }

    @Autowired
    public void setApRequestDao(APRequestDao apRequestDao) {
        this.apRequestDao = apRequestDao;
    }


    @GetMapping("/iniciar/{idAsistente}/{idRequest}")
    public String iniciarChat(@PathVariable int idAsistente, @PathVariable int idRequest, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        chatDao.iniciarChat(usuario.getIdUsuario(), idAsistente, idRequest);

        List<ChatSession> chats = chatDao.getChatsPorUsuario(usuario.getIdUsuario());
        int idChatDestino = chats.stream()
                .filter(c -> c.getIdAsistente() == idAsistente && c.getIdRequest() == idRequest)
                .map(ChatSession::getIdChat)
                .findFirst()
                .orElse(chats.get(0).getIdChat());

        return "redirect:/chat/usuario/sala/" + idChatDestino;
    }

    @GetMapping("/usuario/sala/{idChat}")
    public String salaUsuario(@PathVariable int idChat, HttpSession session, Model model) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        List<ChatSession> chatsActivos = chatDao.getChatsPorUsuario(usuario.getIdUsuario());

        if (idChat == 0) {
            if (!chatsActivos.isEmpty()) {
                return "redirect:/chat/usuario/sala/" + chatsActivos.get(0).getIdChat();
            }
            ChatSession chatVacio = new ChatSession();
            chatVacio.setIdChat(0);
            chatVacio.setNombreAsistente("Ningún chat seleccionado");
            
            model.addAttribute("chatsActivos", chatsActivos);
            model.addAttribute("chatActual", chatVacio);
            model.addAttribute("mensajes", java.util.Collections.emptyList());
            model.addAttribute("nombreUsuario", usuario.getNombre());
            
            return "UsuarioOVI/sala";
        }

        ChatSession chatActual = chatDao.getChat(idChat);
        if (chatActual == null || chatActual.getIdUsuario() != usuario.getIdUsuario()) {
            return "redirect:/UsuarioOVI/dashboard";
        }

        model.addAttribute("chatsActivos", chatsActivos);
        model.addAttribute("chatActual", chatActual);
        model.addAttribute("mensajes", chatDao.getMensajesDelChat(idChat));
        model.addAttribute("nombreUsuario", usuario.getNombre());

        return "UsuarioOVI/sala";
    }

    @PostMapping("/usuario/enviar/{idChat}")
    public String usuarioEnviarMensaje(@PathVariable int idChat, @RequestParam String contenido, HttpSession session) {
        UsuarioOVI usuario = (UsuarioOVI) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/UsuarioOVI/login";
        }

        if (contenido != null && !contenido.trim().isEmpty()) {
            chatDao.guardarMensaje(idChat, usuario.getIdUsuario(), contenido.trim());
        }
        return "redirect:/chat/usuario/sala/" + idChat;
    }

    @GetMapping("/asistente/sala/{idRequest}") 
public String salaAsistente(@PathVariable int idRequest, HttpSession session, Model model) {
    // 1. Verificar seguridad de sesión
    AsistentePersonal asistenteLogueado = (AsistentePersonal) session.getAttribute("asistenteLogueado");
    if (asistenteLogueado == null) {
        return "redirect:/AsistentePersonal/login";
    }

    int idAsistenteReal = asistenteLogueado.getIdAsistente(); 

    // 2. Buscar si ya existe el chat en la BD para esta solicitud
    ChatSession chatActual = null;
    List<ChatSession> todos = chatDao.getTodosLosChats();
    for (ChatSession cs : todos) {
        if (cs.getIdRequest() == idRequest) {
            chatActual = cs;
            break;
        }
    }

    // 3. ¡EL TRUCO!: Si no existe en la BD, creamos un objeto ficticio/temporal en memoria
    // NO llamamos a chatDao.iniciarChat, por lo que NO se guardará nada en la BD aún.
    if (chatActual == null) {
        chatActual = new ChatSession();
        chatActual.setIdChat(0); // Le asignamos ID 0 para reconocerlo en el HTML
        chatActual.setIdRequest(idRequest);
        chatActual.setIdAsistente(idAsistenteReal);
        
        // Opcional: Puedes recuperar el nombre del usuario desde apRequestDao para mostrarlo en la cabecera
        es.uji.ei1027.clubesportiu.model.APRequest sol = apRequestDao.getAPRequest(idRequest);
        if (sol != null) {
            chatActual.setIdUsuario(sol.getIdUsuario());
        }
    }

    // 4. Pasar los datos al modelo
    model.addAttribute("chatsActivos", chatDao.getChatsPorAsistente(idAsistenteReal));
    model.addAttribute("chatActual", chatActual);
    
    // Si el idChat es 0, enviamos una lista vacía de mensajes
    if (chatActual.getIdChat() == 0) {
        model.addAttribute("mensajes", new java.util.ArrayList<MensajeChat>());
    } else {
        model.addAttribute("mensajes", chatDao.getMensajesDelChat(chatActual.getIdChat()));
    }

    return "AsistentePersonal/sala";
}


    @PostMapping("/asistente/enviar/{idChat}")
public String asistenteEnviarMensaje(@PathVariable int idChat, @RequestParam String contenido, @RequestParam(required = false) Integer idRequest, HttpSession session) {
    if (session.getAttribute("asistenteLogueado") == null) {
        return "redirect:/AsistentePersonal/login";
    }

    int idAsistenteReal = 1;

    // Si idChat viene como 0, significa que es el primer mensaje y hay que registrar el chat
    if (idChat == 0 && idRequest != null) {
        es.uji.ei1027.clubesportiu.model.APRequest sol = apRequestDao.getAPRequest(idRequest);
        if (sol != null) {
            // Creamos el chat real en la base de datos al vuelo
            chatDao.iniciarChat(sol.getIdUsuario(), idAsistenteReal, idRequest);
            
            // Buscamos el ID asignado automáticamente al chat recién creado
            List<ChatSession> todos = chatDao.getTodosLosChats();
            for (ChatSession cs : todos) {
                if (cs.getIdRequest() == idRequest) {
                    idChat = cs.getIdChat();
                    break;
                }
            }
        }
    }

    // Guardar el mensaje si el contenido es válido y el chat ya está listo
    if (idChat > 0 && contenido != null && !contenido.trim().isEmpty()) {
        chatDao.guardarMensaje(idChat, idAsistenteReal, contenido.trim());
    }

    // Redirigir de nuevo a la sala (ahora sí con su ID real de chat o el de la solicitud)
    return "redirect:/chat/asistente/sala/" + idRequest;
}
}