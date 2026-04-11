package es.uji.ei1027.clubesportiu.dao;
 
import es.uji.ei1027.clubesportiu.model.ActividadFormacion;
import org.springframework.jdbc.core.RowMapper;
 
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class ActividadFormacionRowMapper implements RowMapper<ActividadFormacion> {
    @Override
    public ActividadFormacion mapRow(ResultSet rs, int rowNum) throws SQLException {
        ActividadFormacion actividad = new ActividadFormacion();
        actividad.setIdActividad(rs.getInt("idActividad"));
        actividad.setIdFormador(rs.getInt("idFormador"));
        actividad.setTitulo(rs.getString("titulo"));
        actividad.setFecha(rs.getDate("fecha").toLocalDate());
        actividad.setDescripcion(rs.getString("descripcion"));
        actividad.setAforoMaximo(rs.getInt("aforoMaximo"));
        return actividad;
    }
}