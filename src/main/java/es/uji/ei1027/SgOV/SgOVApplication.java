package es.uji.ei1027.SgOV;

import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SgOVApplication implements CommandLineRunner {

    private static final Logger log = Logger.getLogger(SgOVApplication.class.getName());

    public static void main(String[] args) {
        // Auto-configura l'aplicació
        new SpringApplicationBuilder(SgOVApplication.class).run(args);
    }

    // Funció principal
    public void run(String... strings) throws Exception {
        log.info("Ací va el meu codi");
    }
}
