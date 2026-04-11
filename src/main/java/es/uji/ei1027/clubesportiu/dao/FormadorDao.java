package es.uji.ei1027.clubesportiu.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.Formador;

@Repository
public class FormadorDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // INSERT
    public void addFormador(Formador formador) {
        jdbcTemplate.update(
                "INSERT INTO Formador VALUES (?, ?, ?, ?, ?)",
                formador.getIdFormador(),
                formador.getNombre(),
                formador.getApellidos(),
                formador.getEmail(),
                formador.getEspecialidad()
        );
    }

    // DELETE
    public void deleteFormador(int idFormador) {
        jdbcTemplate.update(
                "DELETE FROM Formador WHERE idFormador=?",
                idFormador
        );
    }

    // UPDATE
    public void updateFormador(Formador formador) {
        jdbcTemplate.update(
                "UPDATE Formador SET nombre=?, apellidos=?, email=?, especialidad=? WHERE idFormador=?",
                formador.getNombre(),
                formador.getApellidos(),
                formador.getEmail(),
                formador.getEspecialidad(),
                formador.getIdFormador()
        );
    }

    // GET ONE
    public Formador getFormador(int idFormador) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM Formador WHERE idFormador=?",
                    new FormadorRowMapper(),
                    idFormador
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // GET ALL
    public List<Formador> getFormadores() {
        return jdbcTemplate.query(
                "SELECT * FROM Formador",
                new FormadorRowMapper()
        );
    }
}