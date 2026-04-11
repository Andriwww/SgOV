package es.uji.ei1027.clubesportiu.dao;
 
import es.uji.ei1027.clubesportiu.model.Formador;
import org.springframework.jdbc.core.RowMapper;
 
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class FormadorRowMapper implements RowMapper<Formador> {
    @Override
    public Formador mapRow(ResultSet rs, int rowNum) throws SQLException {
        Formador formador = new Formador();
        formador.setIdFormador(rs.getInt("idFormador"));
        formador.setNombre(rs.getString("nombre"));
        formador.setApellidos(rs.getString("apellidos"));
        formador.setEmail(rs.getString("email"));
        formador.setEspecialidad(rs.getString("especialidad"));
        return formador;
    }
}