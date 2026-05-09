package es.uji.ei1027.clubesportiu.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.UsuarioOVI;

@Repository
public class UsuarioOVIDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addUsuarioOVI(UsuarioOVI usuario) {
        jdbcTemplate.update(
                "INSERT INTO usuarioovi (nombre, apellidos, email, telefono, direccion, consentimientorgbd, estadoaceptado) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.isConsentimientoRGBD(),
                usuario.isEstadoAceptado());
    }

    public void deleteUsuarioOVI(int idUsuario) {
        jdbcTemplate.update("DELETE FROM usuarioovi WHERE idusuario = ?", idUsuario);
    }

    public void updateUsuarioOVI(UsuarioOVI usuario) {
        jdbcTemplate.update(
                "UPDATE usuarioovi SET nombre=?, apellidos=?, email=?, telefono=?, direccion=?, consentimientorgbd=?, estadoaceptado=? " +
                        "WHERE idusuario=?",
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
                    "SELECT * FROM usuarioovi WHERE idusuario = ?",
                    new UsuarioOVIRowMapper(),
                    idUsuario);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<UsuarioOVI> getUsuariosOVI() {
        return jdbcTemplate.query(
                "SELECT * FROM usuarioovi",
                new UsuarioOVIRowMapper());
    }

    public UsuarioOVI loadUserByUsername(String username, String password) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM usuario_ovi WHERE (nombre = ? OR email = ?) AND password = ?",
                new UsuarioOVIRowMapper(), 
                username, username, password
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}