package projetPAQ.PAQBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"projetPAQ.PAQBackend"})
public class PaqBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaqBackendApplication.class, args);
	}

}
