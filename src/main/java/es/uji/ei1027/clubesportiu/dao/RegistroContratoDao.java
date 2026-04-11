package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.Estado;
import es.uji.ei1027.clubesportiu.model.RegistroContrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

@Repository
public class RegistroContratoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // INSERT
    public void addRegistroContrato(RegistroContrato contrato) {

        jdbcTemplate.update(
                "INSERT INTO RegistroContrato VALUES (?, ?, ?, ?, ?, ?)",
                contrato.getIdContrato(),
                contrato.getIdAsistente(),
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                contrato.getDocumentoPDF(),
                contrato.getEstado().name()
        );
    }

    // DELETE
    public void deleteRegistroContrato(int idContrato) {
        jdbcTemplate.update(
                "DELETE FROM RegistroContrato WHERE idContrato=?",
                idContrato
        );
    }

    // UPDATE
    public void updateRegistroContrato(RegistroContrato contrato) {

        jdbcTemplate.update(
                "UPDATE RegistroContrato SET idAsistente=?, fechaInicio=?, fechaFin=?, documentoPDF=?, estado=? WHERE idContrato=?",
                contrato.getIdAsistente(),
                contrato.getFechaInicio(),
                contrato.getFechaFin(),
                contrato.getDocumentoPDF(),
                contrato.getEstado().name(),
                contrato.getIdContrato()
        );
    }

    // GET ONE
    public RegistroContrato getRegistroContrato(int idContrato) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM RegistroContrato WHERE idContrato=?",
                    new RegistroContratoRowMapper(),
                    idContrato
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // GET ALL
    public List<RegistroContrato> getRegistroContratos() {
        return jdbcTemplate.query(
                "SELECT * FROM RegistroContrato",
                new RegistroContratoRowMapper()
        );
    }

    // GET BY ASISTENTE
    public List<RegistroContrato> getContratosByAsistente(int idAsistente) {
        return jdbcTemplate.query(
                "SELECT * FROM RegistroContrato WHERE idAsistente=?",
                new RegistroContratoRowMapper(),
                idAsistente
        );
    }
}