package es.uji.ei1027.clubesportiu.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.ei1027.clubesportiu.model.RegistroContrato;

@Repository
public class RegistroContratoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<RegistroContrato> getContratosPorUsuario(int idUsuario) {
        String sql = "SELECT rc.* FROM registrocontrato rc " +
                     "JOIN aprequest r ON rc.idrequest = r.idrequest " +
                     "WHERE r.idusuario = ?";
        return jdbcTemplate.query(sql, new RegistroContratoRowMapper(), idUsuario);
    }

    public List<RegistroContrato> getContratosPorAsistente(int idAsistente) {
    String sql = "SELECT rc.* FROM registrocontrato rc " +
                 "JOIN seleccion s ON rc.idseleccion = s.idseleccion " +
                 "WHERE s.idasistente = ?";
    return jdbcTemplate.query(sql, new RegistroContratoRowMapper(), idAsistente);
}
}