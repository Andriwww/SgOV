package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.AsistentePersonal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.List;

@Repository
public class CandidatoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 1. Guardar un nuevo candidato en la tabla (Ignora si ya existe para evitar errores)
    public void addCandidato(int idRequest, int idAsistente) {
        jdbcTemplate.update(
            "INSERT INTO candidatos (idrequest, idasistente) VALUES (?, ?) ON CONFLICT DO NOTHING",
            idRequest, idAsistente
        );
    }

    // 2. Obtener SOLO LOS IDs de los asistentes asignados (Muy útil para tu assign.html)
    public List<Integer> getIdsCandidatosPorSolicitud(int idRequest) {
        return jdbcTemplate.queryForList(
            "SELECT idasistente FROM candidatos WHERE idrequest = ?", 
            Integer.class, 
            idRequest
        );
    }

    // 3. Obtener los OBJETOS Asistente completos de una solicitud (Para cuando el usuario los vea)
    public List<AsistentePersonal> getAsistentesCandidatos(int idRequest) {
        String sql = "SELECT a.* FROM asistentepersonal a " +
                     "JOIN candidatos c ON a.idasistente = c.idasistente " +
                     "WHERE c.idrequest = ?";
        // Asegúrate de que AsistentePersonalRowMapper existe y está importado
        return jdbcTemplate.query(sql, new AsistentePersonalRowMapper(), idRequest); 
    }

    public void deleteCandidato(int idRequest, int idAsistente) {
        this.jdbcTemplate.update(
            "DELETE FROM candidatos WHERE idrequest = ? AND idasistente = ?",
            idRequest, idAsistente
        );
    }
}