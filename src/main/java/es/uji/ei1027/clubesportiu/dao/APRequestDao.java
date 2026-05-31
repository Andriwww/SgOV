package es.uji.ei1027.clubesportiu.dao;

import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import es.uji.ei1027.clubesportiu.model.Estado;

@Repository
public class APRequestDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // INSERT
    public void addAPRequest(APRequest request) {

    jdbcTemplate.update(
        "INSERT INTO aprequest (idusuario, descripcion, estado) VALUES (?, ?, CAST(? AS estado))",
        request.getIdUsuario(),
        request.getDescripcion(),
        request.getEstado().name()
    );
}

    // DELETE
    public void deleteAPRequest(int idRequest) {
        jdbcTemplate.update(
                "DELETE FROM aprequest WHERE idrequest = ?",
                idRequest
        );
    }

    // UPDATE
    public void updateAPRequest(APRequest request) {
        int idRequest = request.getIdRequest();
        int idUsuario = request.getIdUsuario();
        LocalDate fechaSolicitud = request.getFechaSolicitud();
        String descripcion = request.getDescripcion();
        Estado estado = request.getEstado();

        jdbcTemplate.update(
                "UPDATE aprequest SET idusuario=?, fechasolicitud=?, descripcion=?, estado=? WHERE idrequest=?",
                idUsuario,
                fechaSolicitud,
                descripcion,
                estado.name(),
                idRequest
        );
    }

    public void updateEstadoAPRequest(APRequest request) {
        jdbcTemplate.update(
                "UPDATE aprequest SET estado=CAST(? AS estado) WHERE idrequest=?",
                request.getEstado().name(), 
                request.getIdRequest()
        );
    }

    public APRequest getAPRequest(int idRequest) {
        try {
            String sql = "SELECT idrequest, fechasolicitud, descripcion, estado, idusuario, idseleccion FROM aprequest WHERE idrequest = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                APRequest req = new APRequest();
                req.setIdRequest(rs.getInt("idrequest"));
                
                // 1. SOLUCIÓN FECHA: Extraer directamente como LocalDate
                req.setFechaSolicitud(rs.getObject("fechasolicitud", java.time.LocalDate.class));
                
                req.setDescripcion(rs.getString("descripcion"));
                
                // 2. SOLUCIÓN ESTADO: Convertir el String al Enum (pasándolo a mayúsculas por seguridad)
                String estadoStr = rs.getString("estado");
                if (estadoStr != null) {
                    req.setEstado(Estado.valueOf(estadoStr.toLowerCase())); 
                    // 💡 Nota: Si los valores de tu Enum están en minúsculas, quita el .toUpperCase()
                }
                
                // 3. SOLUCIÓN ID USUARIO: 'I' mayúscula
                req.setIdUsuario(rs.getInt("idusuario"));
                
                // Mapeo del idseleccion para el control de flujo
                req.setIdSeleccion(rs.getObject("idseleccion", Integer.class));
                
                return req;
            }, idRequest);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null; 
        }
    }

    // GET ALL
    public List<APRequest> getAPRequests() {
        return jdbcTemplate.query(
                "SELECT * FROM aprequest",
                new APRequestRowMapper()
        );
    }

    // GET BY USER
    public List<APRequest> getAPRequestsByUsuario(int idUsuario) {
        return jdbcTemplate.query(
                "SELECT * FROM aprequest WHERE idusuario=?",
                new APRequestRowMapper(),
                idUsuario
        );
    }


   public List<APRequest> getAPRequestsByAsistente(int idAsistente) {
        String sql = "SELECT r.* FROM aprequest r " +
                    "JOIN seleccion s ON r.idrequest = s.idseleccion " + 
                    "WHERE s.idasistente = ?";
                    
        return jdbcTemplate.query(sql, new APRequestRowMapper(), idAsistente);
    }

    public void asignarAsistente(int idSolicitud, int idAsistente, int idUsuario) {
        
        // 1. Insertamos en 'seleccion' y le pedimos a PostgreSQL que nos devuelva el ID autogenerado
        // Ojo: El estado de la SELECCIÓN sí requiere un valor por tu base de datos ('pendiente' por defecto)
        String sqlInsertSeleccion = 
            "INSERT INTO seleccion (fechaseleccion, estado, idusuario, idasistente) " +
            "VALUES (CURRENT_DATE, 'pendiente'::estado, ?, ?) RETURNING idseleccion";
        
        Integer idSeleccionGenerado = jdbcTemplate.queryForObject(
            sqlInsertSeleccion, 
            Integer.class, 
            idUsuario, 
            idAsistente
        );
        
        // 2. Vinculamos este nuevo idseleccion a la solicitud (¡Sin tocar el estado!)
        String sqlUpdateSolicitud = 
            "UPDATE aprequest SET idseleccion = ? WHERE idrequest = ?";
            
        jdbcTemplate.update(sqlUpdateSolicitud, idSeleccionGenerado, idSolicitud);
    }
}