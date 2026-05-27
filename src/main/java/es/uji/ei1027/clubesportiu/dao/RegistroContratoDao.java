package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.RegistroContrato;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.List;

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
}