package es.uji.ei1027.clubesportiu.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.ActividadFormacion;

@Repository
public class ActividadFormacionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // INSERT
    public void addActividadFormacion(ActividadFormacion actividad) {
        jdbcTemplate.update(
                "INSERT INTO ActividadFormacion VALUES (?, ?, ?, ?, ?, ?)",
                actividad.getIdActividad(),
                actividad.getIdFormador(),
                actividad.getTitulo(),
                actividad.getFecha(),
                actividad.getDescripcion(),
                actividad.getAforoMaximo()
        );
    }

    // DELETE
    public void deleteActividadFormacion(int idActividad) {
        jdbcTemplate.update(
                "DELETE FROM ActividadFormacion WHERE idActividad=?",
                idActividad
        );
    }

    // UPDATE
    public void updateActividadFormacion(ActividadFormacion actividad) {
        jdbcTemplate.update(
                "UPDATE ActividadFormacion SET idFormador=?, titulo=?, fecha=?, descripcion=?, aforoMaximo=? WHERE idActividad=?",
                actividad.getIdFormador(),
                actividad.getTitulo(),
                actividad.getFecha(),
                actividad.getDescripcion(),
                actividad.getAforoMaximo(),
                actividad.getIdActividad()
        );
    }

    // GET ONE
    public ActividadFormacion getActividadFormacion(int idActividad) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM ActividadFormacion WHERE idActividad=?",
                    new ActividadFormacionRowMapper(),
                    idActividad
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // GET ALL
    public List<ActividadFormacion> getActividadesFormacion() {
        return jdbcTemplate.query(
                "SELECT * FROM ActividadFormacion",
                new ActividadFormacionRowMapper()
        );
    }

    // GET BY FORMADOR
    public List<ActividadFormacion> getActividadesByFormador(int idFormador) {
        return jdbcTemplate.query(
                "SELECT * FROM ActividadFormacion WHERE idFormador=?",
                new ActividadFormacionRowMapper(),
                idFormador
        );
    }
}