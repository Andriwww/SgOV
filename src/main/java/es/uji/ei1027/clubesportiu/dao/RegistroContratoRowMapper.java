package es.uji.ei1027.clubesportiu.dao;
 
import es.uji.ei1027.clubesportiu.model.Estado;
import es.uji.ei1027.clubesportiu.model.RegistroContrato;
import org.springframework.jdbc.core.RowMapper;
 
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class RegistroContratoRowMapper implements RowMapper<RegistroContrato> {
    @Override
    public RegistroContrato mapRow(ResultSet rs, int rowNum) throws SQLException {
        RegistroContrato contrato = new RegistroContrato();
        contrato.setIdContrato(rs.getInt("idContrato"));
        contrato.setIdAsistente(rs.getInt("idAsistente"));
        contrato.setFechaInicio(rs.getDate("fechaInicio").toLocalDate());
        java.sql.Date fechaFin = rs.getDate("fechaFin");
        contrato.setFechaFin(fechaFin != null ? fechaFin.toLocalDate() : null);
        contrato.setDocumentoPDF(rs.getString("documentoPDF"));
        contrato.setEstado(Estado.valueOf(rs.getString("estado")));
        return contrato;
    }
}
 