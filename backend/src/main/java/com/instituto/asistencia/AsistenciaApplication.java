package com.instituto.asistencia;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class AsistenciaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsistenciaApplication.class, args);
	}

}
