package es.uji.ei1027.clubesportiu.dao;

import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.APRequest;
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
        "INSERT INTO aprequest (idusuario, fechasolicitud, descripcion, estado) VALUES (?, ?, ?, CAST(? AS estado))",
        request.getIdUsuario(),
        java.sql.Date.valueOf(request.getFechaSolicitud()),
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
                "UPDATE aprequest SET idusuario=?, fechaSolicitud=?, descripcion=?, estado=? WHERE idrequest=?",
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

    // GET ONE
    public APRequest getAPRequest(int idRequest) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM aprequest WHERE idrequest=?",
                    new APRequestRowMapper(),
                    idRequest
            );
        } catch (EmptyResultDataAccessException e) {
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
}