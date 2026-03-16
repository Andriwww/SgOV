package es.uji.ei1027.clubesportiu.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.AsistenciaFormacion;

@Repository
public class AsistenciaFormacionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // INSERT
    public void addAsistenciaFormacion(AsistenciaFormacion asistencia) {
        jdbcTemplate.update(
                "INSERT INTO AsistenciaFormacion VALUES (?, ?, ?, ?, ?, ?, ?)",
                asistencia.getIdAsistencia(),
                asistencia.getIdActividad(),
                asistencia.getIdUsuario(),
                asistencia.getIdAsistente(),
                asistencia.isAsistio(),
                asistencia.isCertificadoGenerado(),
                asistencia.getFecha()
        );
    }

    // DELETE
    public void deleteAsistenciaFormacion(int idAsistencia) {
        jdbcTemplate.update(
                "DELETE FROM AsistenciaFormacion WHERE idAsistencia=?",
                idAsistencia
        );
    }

    // UPDATE
    public void updateAsistenciaFormacion(AsistenciaFormacion asistencia) {
        jdbcTemplate.update(
                "UPDATE AsistenciaFormacion SET idActividad=?, idUsuario=?, idAsistente=?, asistio=?, certificadoGenerado=?, fecha=? WHERE idAsistencia=?",
                asistencia.getIdActividad(),
                asistencia.getIdUsuario(),
                asistencia.getIdAsistente(),
                asistencia.isAsistio(),
                asistencia.isCertificadoGenerado(),
                asistencia.getFecha(),
                asistencia.getIdAsistencia()
        );
    }

    // GET ONE
    public AsistenciaFormacion getAsistenciaFormacion(int idAsistencia) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM AsistenciaFormacion WHERE idAsistencia=?",
                    new BeanPropertyRowMapper<>(AsistenciaFormacion.class),
                    idAsistencia
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // GET ALL
    public List<AsistenciaFormacion> getAsistenciasFormacion() {
        return jdbcTemplate.query(
                "SELECT * FROM AsistenciaFormacion",
                new BeanPropertyRowMapper<>(AsistenciaFormacion.class)
        );
    }
    
    // GET BY ACTIVIDAD
    public List<AsistenciaFormacion> getAsistenciasByActividad(int idActividad) {
        return jdbcTemplate.query(
                "SELECT * FROM AsistenciaFormacion WHERE idActividad=?",
                new BeanPropertyRowMapper<>(AsistenciaFormacion.class),
                idActividad
        );
    }
}