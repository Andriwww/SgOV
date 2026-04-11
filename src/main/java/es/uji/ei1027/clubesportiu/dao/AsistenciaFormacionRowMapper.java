package es.uji.ei1027.clubesportiu.dao;
 
import es.uji.ei1027.clubesportiu.model.AsistenciaFormacion;
import org.springframework.jdbc.core.RowMapper;
 
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class AsistenciaFormacionRowMapper implements RowMapper<AsistenciaFormacion> {
    @Override
    public AsistenciaFormacion mapRow(ResultSet rs, int rowNum) throws SQLException {
        AsistenciaFormacion asistencia = new AsistenciaFormacion();
        asistencia.setIdAsistencia(rs.getInt("idAsistencia"));
        asistencia.setIdActividad(rs.getInt("idActividad"));
        asistencia.setIdUsuario(rs.getInt("idUsuario"));
        asistencia.setIdAsistente(rs.getInt("idAsistente"));
        asistencia.setAsistio(rs.getBoolean("asistio"));
        asistencia.setCertificadoGenerado(rs.getBoolean("certificadoGenerado"));
        asistencia.setFecha(rs.getDate("fecha").toLocalDate());
        return asistencia;
    }
}
 