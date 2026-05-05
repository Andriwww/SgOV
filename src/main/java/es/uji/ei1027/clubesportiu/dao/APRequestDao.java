package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.Estado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

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
        "INSERT INTO APRequest (idUsuario, fechaSolicitud, descripcion, estado) VALUES (?, ?, ?, ?)",
        request.getIdUsuario(),
        java.sql.Date.valueOf(request.getFechaSolicitud()),
        request.getDescripcion(),
        request.getEstado().name()
    );
}

    // DELETE
    public void deleteAPRequest(int idRequest) {
        jdbcTemplate.update(
                "DELETE FROM APRequest WHERE idRequest = ?",
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
                "UPDATE APRequest SET idUsuario=?, fechaSolicitud=?, descripcion=?, estado=? WHERE idRequest=?",
                idUsuario,
                fechaSolicitud,
                descripcion,
                estado.name(),
                idRequest
        );
    }

    // GET ONE
    public APRequest getAPRequest(int idRequest) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM APRequest WHERE idRequest=?",
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
                "SELECT * FROM APRequest",
                new APRequestRowMapper()
        );
    }

    // GET BY USER
    public List<APRequest> getAPRequestsByUsuario(int idUsuario) {
        return jdbcTemplate.query(
                "SELECT * FROM APRequest WHERE idUsuario=?",
                new APRequestRowMapper(),
                idUsuario
        );
    }
}