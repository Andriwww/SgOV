package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.Estado;
import es.uji.ei1027.clubesportiu.model.Seleccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository // En Spring els DAOs van anotats amb @Repository
public class SeleccionDao {
    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addSeleccion(Seleccion seleccion) {
        int idSeleccion = seleccion.getIdSeleccion();
        LocalDate fechaSeleccion = seleccion.getFechaSeleccion();
        Estado estado = seleccion.getEstado();

        jdbcTemplate.update(
                "INSERT INTO seleccion VALUES(?, ?, ?)",
                idSeleccion, fechaSeleccion, estado.name());
    }

    public void deleteSeleccion(int idSeleccion) {
        jdbcTemplate.update("DELETE FROM seleccion WHERE idSeleccion =?", idSeleccion);
    }

    public void updateSeleccion(Seleccion seleccion) {
        int idSeleccion = seleccion.getIdSeleccion();
        LocalDate fechaSeleccion = seleccion.getFechaSeleccion();
        Estado estado = seleccion.getEstado();

        jdbcTemplate.update("UPDATE seleccion SET fechaSeleccion=?, estado=? WHERE idSeleccion=?",
                fechaSeleccion, estado.name(), idSeleccion);
    }

    public Seleccion getSeleccion(int idSeleccion) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM seleccion WHERE idSeleccion =?",
                    new BeanPropertyRowMapper<>(Seleccion.class),
                    idSeleccion);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Seleccion> getSelecciones() {
        return jdbcTemplate.query("SELECT * FROM seleccion", new BeanPropertyRowMapper<>(Seleccion.class));
    }
}
