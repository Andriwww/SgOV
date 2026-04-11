package es.uji.ei1027.clubesportiu.dao;
 
import es.uji.ei1027.clubesportiu.model.APRequest;
import es.uji.ei1027.clubesportiu.model.Estado;
import org.springframework.jdbc.core.RowMapper;
 
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class APRequestRowMapper implements RowMapper<APRequest> {
    @Override
    public APRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        APRequest request = new APRequest();
        request.setIdRequest(rs.getInt("idRequest"));
        request.setIdUsuario(rs.getInt("idUsuario"));
        request.setFechaSolicitud(rs.getDate("fechaSolicitud").toLocalDate());
        request.setDescripcion(rs.getString("descripcion"));
        request.setEstado(Estado.valueOf(rs.getString("estado")));
        return request;
    }
}