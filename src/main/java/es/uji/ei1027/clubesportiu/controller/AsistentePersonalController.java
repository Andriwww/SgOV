package es.uji.ei1027.clubesportiu.controller;

import java.util.List;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.ei1027.clubesportiu.dao.AsistentePersonalDao;
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.RegistroContrato;
import es.uji.ei1027.clubesportiu.validator.AsistentePersonalValidator;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/AsistentePersonal")
public class AsistentePersonalController {

    private AsistentePersonalDao asistentePersonalDao;
    private es.uji.ei1027.clubesportiu.dao.APRequestDao apRequestDao;
    private es.uji.ei1027.clubesportiu.dao.RegistroContratoDao registroContratoDao;

    @Autowired
    public void setAsistentePersonalDao(AsistentePersonalDao dao) {
        this.asistentePersonalDao = dao;
    }

    @Autowired
    public void setAPRequestDao(es.uji.ei1027.clubesportiu.dao.APRequestDao dao) {
        this.apRequestDao = dao;
    }

    @Autowired
    public void setRegistroContratoDao(es.uji.ei1027.clubesportiu.dao.RegistroContratoDao dao) {
        this.registroContratoDao = dao;
    }

    // LISTAR ASISTENTES
    @RequestMapping("/list")
    public String list(Model model) {

        model.addAttribute("asistentes",
            asistentePersonalDao.getAsistentesPersonales());

        model.addAttribute("numSolicitudes",
            asistentePersonalDao.countAsistentesPendientes());

        return "AsistentePersonal/list";
    }


    @RequestMapping("/list/pendientes")
    public String listPendientes(Model model) {

        model.addAttribute("asistentes",
            asistentePersonalDao.getAsistentesPersonalesPendientes());

        return "AsistentePersonal/TecnicoSolicitudes";
    }



    @RequestMapping("/main")
    public String main(HttpSession session, Model model) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/";
        }
        model.addAttribute("asistente", asistente);
        return "AsistentePersonal/main";
    }




    // MOSTRAR FORMULARIO DE ALTA / SOLICITUD (GET)
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model model) {
        model.addAttribute("asistente", new AsistentePersonal());
        return "AsistentePersonal/register"; // Renderiza el archivo register.html
    }





    // PROCESAR FORMULARIO DE ALTA / SOLICITUD (POST)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerSubmit(@ModelAttribute("asistente") AsistentePersonal asistente,
                                BindingResult result, HttpSession session) {

        AsistentePersonalValidator validator = new AsistentePersonalValidator(asistentePersonalDao);
        validator.validate(asistente, result);

        if (result.hasErrors()) {
            return "AsistentePersonal/register"; // Si falla, recarga register.html mostrando los mensajes de error
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        String passEncriptada = passwordEncryptor.encryptPassword(asistente.getContraseña());
    
        asistente.setContraseña(passEncriptada);
        asistente.setEstadoAceptado(false); // Por defecto, el asistente no está aceptado

        asistentePersonalDao.addAsistentePersonal(asistente);
        session.setAttribute("asistenteLogueado", asistente); // Guardamos el asistente recién registrado en sesión
        return "redirect:/AsistentePersonal/esperaValidacion"; // Redirige a la página de espera de validación
    }






    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model Model) {
        Model.addAttribute("asistente", new AsistentePersonal());
        return "AsistentePersonal/login";
    }






    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginSubmit(@ModelAttribute("asistente") AsistentePersonal usuario,
                              BindingResult result, HttpSession session, Model model) {

        if (result.hasErrors()) {
            return "AsistentePersonal/login";
        }

        AsistentePersonal asistenteBD = asistentePersonalDao.getAsistentePersonalByEmail(usuario.getEmail());

        if (asistenteBD == null) {
            result.rejectValue("email", "bad-credentials", "El correo electrónico o la contraseña son incorrectos.");
            model.addAttribute("error", "Credenciales incorrectas");
            return "AsistentePersonal/login";
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
        if (!passwordEncryptor.checkPassword(usuario.getContraseña(), asistenteBD.getContraseña())) {
            model.addAttribute("error", "Credenciales incorrectas");
            return "AsistentePersonal/login";
        }

        session.removeAttribute("error");
        session.setAttribute("asistenteLogueado", asistenteBD);
        return "redirect:/AsistentePersonal/esperaValidacion";
    }





     // MOSTRAR PERFIL DEL ASISTENTE
     @RequestMapping("/perfil")
     public String perfil(Model model, HttpSession session) {
         AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
            if (asistente == null) {
                return "redirect:/AsistentePersonal/login"; // Si no hay asistente en sesión, redirige al login
            }
         AsistentePersonal asistenteBD = asistentePersonalDao.getAsistentePersonalByEmail(asistente.getEmail());
         model.addAttribute("asistente", asistenteBD);
         return "AsistentePersonal/perfil"; // Renderiza el archivo perfil.html
     }





    // MOSTRAR FORMULARIO DE EDICIÓN DE PERFIL (GET)
    @RequestMapping("/update")
    public String editForm(Model model, HttpSession session) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login"; // Si no hay asistente en sesión, redirige al login
        }
        model.addAttribute("asistente", asistente);
        return "AsistentePersonal/update"; // Renderiza el archivo update.html
    }





    // PROCESAR FORMULARIO DE EDICIÓN DE PERFIL (POST)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String editSubmit(@ModelAttribute("asistente") AsistentePersonal asistente,
                             BindingResult result) {

        AsistentePersonalValidator validator = new AsistentePersonalValidator(asistentePersonalDao);
        validator.validate(asistente, result);

        if (result.hasErrors()) {
            return "AsistentePersonal/update"; // Si falla la edición, recarga update.html reteniendo los campos erróneos
        }

        AsistentePersonal asistenteBD = asistentePersonalDao.getAsistentePersonalByEmail(asistente.getEmail());
        if (asistenteBD != null && !(asistenteBD.getIdAsistente() == asistente.getIdAsistente())) {
            result.rejectValue("email", "duplicate", "El email ya está registrado por otro asistente");
            return "AsistentePersonal/update";
        }

        asistente.setContraseña(asistenteBD.getContraseña()); // Mantenemos la contraseña actual sin cambios
        asistentePersonalDao.updateAsistentePersonal(asistente);
        return "redirect:main";
    }






    // ELIMINAR ASISTENTE
    @RequestMapping("/delete/{idAsistente}")
    public String delete(@PathVariable int idAsistente) {
        asistentePersonalDao.deleteAsistentePersonal(idAsistente);
        return "redirect:/";
    }




    @RequestMapping("/aceptar/{idAsistente}")
    public String aceptar(@PathVariable int idAsistente) {

        AsistentePersonal asistente = asistentePersonalDao.getAsistentePersonal(idAsistente);

        if (asistente != null) {
            asistente.setEstadoAceptado(true);
            asistentePersonalDao.updateAsistentePersonal(asistente);
        }

        return "redirect:/AsistentePersonal/list/pendientes";
    }

    @RequestMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping("/rechazar/{idAsistente}")
    public String rechazar(@PathVariable int idAsistente) {
        asistentePersonalDao.deleteAsistentePersonal(idAsistente);
        return "redirect:/AsistentePersonal/list/pendientes";
    }

    @RequestMapping("/eliminar/{idAsistente}")
    public String eliminar(@PathVariable int idAsistente) {
        asistentePersonalDao.deleteAsistentePersonal(idAsistente);
        return "redirect:/AsistentePersonal/list";
    }


    @RequestMapping(value = "/esperaValidacion", method = RequestMethod.GET)
    public String esperaValidacion(HttpSession session, Model model) {
        AsistentePersonal asistenteSesion = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistenteSesion == null) {
            return "redirect:/AsistentePersonal/login";
        }
        
        // 2. CORRECCIÓN: Consultar los datos actualizados directamente al DAO (Base de datos)
        AsistentePersonal asistenteReal = asistentePersonalDao.getAsistentePersonalByEmail(asistenteSesion.getEmail());
        
        // 3. Comprobamos el estado del objeto recién traído de la base de datos
        if (asistenteReal != null && asistenteReal.isEstadoAceptado()) {
            // Opcional pero recomendado: Actualizamos la sesión con el nuevo estado del usuario
            session.setAttribute("asistenteLogueado", asistenteReal);
            
            // Redirigimos al menú principal
            return "redirect:/AsistentePersonal/main"; 
        }
        
        // Si sigue sin estar aceptado, se queda en la pantalla de espera
        return "AsistentePersonal/esperaValidacion"; 
    }

    // 1. VISUALIZACIÓN DE SOLICITUDES
    @RequestMapping(value = "/solicitudes", method = RequestMethod.GET)
    public String misSolicitudes(HttpSession session, Model model) {
        // Control de seguridad: Verificar sesión activa del asistente
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login";
        }

        // Obtener las solicitudes asignadas a este asistente cruzando con la tabla Selección
        List<APRequest> solicitudes = apRequestDao.getAPRequestsByAsistente(asistente.getIdAsistente());

        // Pasamos la variable con el nombre exacto que requiere el HTML: "solicitudesAsistente"
        model.addAttribute("solicitudesAsistente", solicitudes);

        return "AsistentePersonal/solicitudes";
    }

    // 2. ACCIÓN DE ACEPTAR LA SOLICITUD
    @RequestMapping(value = "/solicitudes/aceptar/{id}", method = RequestMethod.GET)
    public String aceptarSolicitud(@PathVariable("id") int idRequest, HttpSession session) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login";
        }

        APRequest request = apRequestDao.getAPRequest(idRequest);
        if (request != null) {
            request.setEstado(es.uji.ei1027.clubesportiu.model.Estado.aprobada); // Cambia al Enum en mayúsculas
            apRequestDao.updateEstadoAPRequest(request);
        }

        return "redirect:/AsistentePersonal/solicitudes";
    }

    // 3. ACCIÓN DE RECHAZAR LA SOLICITUD
    @RequestMapping(value = "/solicitudes/rechazar/{id}", method = RequestMethod.GET)
    public String rechazarSolicitud(@PathVariable("id") int idRequest, HttpSession session) {
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login";
        }

        APRequest request = apRequestDao.getAPRequest(idRequest);
        if (request != null) {
            request.setEstado(es.uji.ei1027.clubesportiu.model.Estado.rechazada); 
            apRequestDao.updateEstadoAPRequest(request);
        }

        return "redirect:/AsistentePersonal/solicitudes";
    }


    @RequestMapping(value = "/contratos", method = RequestMethod.GET)
    public String misContratos(HttpSession session, Model model) {
        // 1. Validar seguridad de la sesión
        AsistentePersonal asistente = (AsistentePersonal) session.getAttribute("asistenteLogueado");
        if (asistente == null) {
            return "redirect:/AsistentePersonal/login";
        }

        // 2. Pasar el usuario explícitamente para evitar problemas en el navbar de Thymeleaf
        model.addAttribute("usuarioLogueado", asistente);

        // 3. Buscar los contratos en el DAO usando el ID del asistente
        List<RegistroContrato> contratos = registroContratoDao.getContratosPorAsistente(asistente.getIdAsistente());
        
        // 💡 IMPORTANTE: El nombre que guardes aquí en el model debe coincidir con el th:each del HTML
        model.addAttribute("contratosAsistente", contratos);

        return "AsistentePersonal/contratos";
    }
}