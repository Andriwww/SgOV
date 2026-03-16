package es.uji.ei1027.clubesportiu.dao;

import es.uji.ei1027.clubesportiu.model.Seleccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository // En Spring els DAOs van anotats amb @Repository
public class SeleccionDao {
    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addSeleccion(Seleccion seleccion) {
        int athleteIdentificator = athlete.getId();
        String nameAndSurnames = athlete.getNameAndSurnames();
        LocalDate dateOfBirth = Date.valueOf(athlete.getDateOfBirth()).toLocalDate();
        String email = athlete.getEmail();

        jdbcTemplate.update(
                "INSERT INTO athlete VALUES(?, ?, ?, ?)",
                athleteIdentificator, nameAndSurnames, dateOfBirth, email);
    }

    public void deleteAthlete(int athleteIdentificator) {
        jdbcTemplate.update("DELETE FROM athlete WHERE athleteIdentificator =?", athleteIdentificator);
    }

    public void updateAthlete(Athlete athlete) {
        int athleteIdentificator = athlete.getId();
        String nameAndSurnames = athlete.getNameAndSurnames();
        LocalDate dateOfBirth = Date.valueOf(athlete.getDateOfBirth()).toLocalDate();
        String email = athlete.getEmail();

        jdbcTemplate.update("UPDATE athlete SET nameAndSurnames =?, dateOfBirth=?, email=? WHERE athleteIdentificator =?",
                nameAndSurnames, dateOfBirth, email, athleteIdentificator);
    }

    public Athlete getAthlete(int athleteIdentificator) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM athlete WHERE athleteIdentificator =?",
                    new AthleteRowMapper(),
                    athleteIdentificator);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Athlete> getAthletes() {
        try {
            return jdbcTemplate.query("SELECT * FROM athlete", new AthleteRowMapper());
        }
        catch (EmptyResultDataAccessException e) {
            return new ArrayList<Athlete>();
        }
    }
}
