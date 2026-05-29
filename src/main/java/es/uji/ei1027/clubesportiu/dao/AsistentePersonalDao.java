package es.uji.ei1027.clubesportiu.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.AsistentePersonal;

@Repository
public class AsistentePersonalDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // INSERT: Guarda el registro completo proveniente de register.html
    public void addAsistentePersonal(AsistentePersonal asistente) {
        jdbcTemplate.update(
            "INSERT INTO AsistentePersonal (nombre, apellidos, email, contraseña, telefono, disponibilidad, estadoAceptado, activo, zona, preferencias, puntuacion, consentimientoRGBD) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            asistente.getNombre(),
            asistente.getApellidos(),
            asistente.getEmail(),
            asistente.getContraseña(),
            asistente.getTelefono(),
            asistente.getDisponibilidad(),
            asistente.isEstadoAceptado(),
            asistente.isActivo(),
            asistente.getZona(),
            asistente.getPreferencias(),
            asistente.getPuntuacion(),
            asistente.isConsentimientoRGBD()
        );
    }

    // UPDATE: Actualiza todos los parámetros del asistente (incluyendo los de update.html)
    public void updateAsistentePersonal(AsistentePersonal asistente) {
        jdbcTemplate.update(
            "UPDATE AsistentePersonal SET nombre=?, apellidos=?, email=?, contraseña=?, telefono=?, disponibilidad=?, estadoAceptado=?, activo=?, zona=?, preferencias=?, puntuacion=?, consentimientoRGBD=? WHERE idAsistente=?",
            asistente.getNombre(),
            asistente.getApellidos(),
            asistente.getEmail(),
            asistente.getContraseña(),
            asistente.getTelefono(),
            asistente.getDisponibilidad(),
            asistente.isEstadoAceptado(),
            asistente.isActivo(),
            asistente.getZona(),
            asistente.getPreferencias(),
            asistente.getPuntuacion(),
            asistente.isConsentimientoRGBD(),
            asistente.getIdAsistente()
        );
    }

    // DELETE
    public void deleteAsistentePersonal(int idAsistente) {
        jdbcTemplate.update("DELETE FROM AsistentePersonal WHERE idAsistente=?", idAsistente);
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

    

    public List<AsistentePersonal> buscarCompatibles(APRequest request) {
        return jdbcTemplate.query(
                "SELECT * FROM AsistentePersonal",
                new AsistentePersonalRowMapper()
        );
    }

    public AsistentePersonal getAsistentePersonalByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM AsistentePersonal WHERE email=?",
                    new AsistentePersonalRowMapper(),
                    email
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Comprueba si el email ya existe en otro registro diferente al actual
    public boolean existeEmail(String email, int idAsistente) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM AsistentePersonal WHERE email = ? AND idAsistente != ?",
                Integer.class,
                email,
                idAsistente
        );
        return count != null && count > 0;
    }

        // Obtiene la lista de asistentes personales pendientes de aceptación
        public List<AsistentePersonal> getAsistentesPersonalesPendientes() {
            return jdbcTemplate.query(
                    "SELECT * FROM AsistentePersonal WHERE estadoAceptado = false",
                    new AsistentePersonalRowMapper()
            );
        }
}