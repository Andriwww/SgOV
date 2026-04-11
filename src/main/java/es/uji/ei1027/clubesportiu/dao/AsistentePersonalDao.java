package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AsistentePersonalDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // INSERT
    public void addAsistentePersonal(AsistentePersonal asistente) {

        jdbcTemplate.update(
                "INSERT INTO AsistentePersonal VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                asistente.getIdAsistente(),
                asistente.getNombre(),
                asistente.getApellidos(),
                asistente.getEmail(),
                asistente.getTelefono(),
                asistente.getDisponibilidad(),
                asistente.isEstadoAceptado(),
                asistente.isActivo()
        );
    }

    // DELETE
    public void deleteAsistentePersonal(int idAsistente) {
        jdbcTemplate.update(
                "DELETE FROM AsistentePersonal WHERE idAsistente = ?",
                idAsistente
        );
    }

    // UPDATE
    public void updateAsistentePersonal(AsistentePersonal asistente) {

        jdbcTemplate.update(
                "UPDATE AsistentePersonal SET nombre=?, apellidos=?, email=?, telefono=?, disponibilidad=?, estadoAceptado=?, activo=? WHERE idAsistente=?",
                asistente.getNombre(),
                asistente.getApellidos(),
                asistente.getEmail(),
                asistente.getTelefono(),
                asistente.getDisponibilidad(),
                asistente.isEstadoAceptado(),
                asistente.isActivo(),
                asistente.getIdAsistente()
        );
    }

    // GET ONE
    public AsistentePersonal getAsistentePersonal(int idAsistente) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM AsistentePersonal WHERE idAsistente=?",
                    new AsistentePersonalRowMapper(),
                    idAsistente
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // GET ALL
    public List<AsistentePersonal> getAsistentesPersonales() {
        return jdbcTemplate.query(
                "SELECT * FROM AsistentePersonal",
                new AsistentePersonalRowMapper()
        );
    }
}