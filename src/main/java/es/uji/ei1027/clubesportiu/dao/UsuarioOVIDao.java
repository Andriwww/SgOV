package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.UsuarioOVI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UsuarioOVIDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addUsuarioOVI(UsuarioOVI usuario) {
        jdbcTemplate.update(
                "INSERT INTO usuarioOVI (idUsuario, nombre, apellidos, email, telefono, direccion, consentimientoRGBD, estadoAceptado) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.isConsentimientoRGBD(),
                usuario.isEstadoAceptado());
    }

    public void deleteUsuarioOVI(int idUsuario) {
        jdbcTemplate.update("DELETE FROM usuarioOVI WHERE idUsuario = ?", idUsuario);
    }

    public void updateUsuarioOVI(UsuarioOVI usuario) {
        jdbcTemplate.update(
                "UPDATE usuarioOVI SET nombre=?, apellidos=?, email=?, telefono=?, direccion=?, consentimientoRGBD=?, estadoAceptado=? " +
                        "WHERE idUsuario=?",
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.isConsentimientoRGBD(),
                usuario.isEstadoAceptado(),
                usuario.getIdUsuario());
    }

    public UsuarioOVI getUsuarioOVI(int idUsuario) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM usuarioOVI WHERE idUsuario = ?",
                    new BeanPropertyRowMapper<>(UsuarioOVI.class),
                    idUsuario);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<UsuarioOVI> getUsuariosOVI() {
        return jdbcTemplate.query(
                "SELECT * FROM usuarioOVI",
                new BeanPropertyRowMapper<>(UsuarioOVI.class));
    }
}