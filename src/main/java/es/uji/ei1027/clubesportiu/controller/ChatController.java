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

import es.uji.ei1027.clubesportiu.dao.ChatDao;
import es.uji.ei1027.clubesportiu.model.ChatSession;
import es.uji.ei1027.clubesportiu.model.MensajeChat;
import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private ChatDao chatDao;

    @Autowired
    public void setChatDao(ChatDao chatDao) {
        this.chatDao = chatDao;
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
            chatDao.guardarMensaje(idChat, "USUARIO", contenido.trim());
        }
        return "redirect:/chat/usuario/sala/" + idChat;
    }

    @GetMapping("/asistente/sala/{idChat}")
    public String salaAsistente(@PathVariable int idChat, HttpSession session, Model model) {
        Object asistente = session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login";
        }

        int idAsistente = 1;

        ChatSession chatActual = chatDao.getChat(idChat);
        if (chatActual == null || chatActual.getIdAsistente() != idAsistente) {
            return "redirect:/AsistentePersonal/dashboard";
        }

        model.addAttribute("chatsActivos", chatDao.getChatsPorAsistente(idAsistente));
        model.addAttribute("chatActual", chatActual);
        model.addAttribute("mensajes", chatDao.getMensajesDelChat(idChat));

        return "AsistentePersonal/sala";
    }

    @PostMapping("/asistente/enviar/{idChat}")
    public String asistenteEnviarMensaje(@PathVariable int idChat, @RequestParam String contenido, HttpSession session) {
        if (session.getAttribute("asistenteLogueado") == null) {
            return "redirect:/AsistentePersonal/login";
        }

        if (contenido != null && !contenido.trim().isEmpty()) {
            chatDao.guardarMensaje(idChat, "ASISTENTE", contenido.trim());
        }
        return "redirect:/chat/asistente/sala/" + idChat;
    }
}