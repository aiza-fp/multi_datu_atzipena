package com.AM3Ethazi.app;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.AM3Ethazi.app.entitateak.Idazlea;
import com.AM3Ethazi.app.entitateak.Liburua;
import com.AM3Ethazi.app.repository.IdazleaRepository;
import com.AM3Ethazi.app.repository.LiburuaRepository;

@SpringBootApplication
@ComponentScan(basePackages = "com.AM3Ethazi.app")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public CommandLineRunner initData(IdazleaRepository idazleaRepo, LiburuaRepository liburuRepo) {
		return args -> {
			// Create authors
			Idazlea idazlea1 = new Idazlea();
			idazlea1.setIzena("J.K. Rowling");
			
			Idazlea idazlea2 = new Idazlea();
			idazlea2.setIzena("George R.R. Martin");
			
			Idazlea idazlea3 = new Idazlea();
			idazlea3.setIzena("Stephen King");
			
			// Save authors
			idazleaRepo.save(idazlea1);
			idazleaRepo.save(idazlea2);
			idazleaRepo.save(idazlea3);
			
			// Create books
			Liburua liburua1 = new Liburua();
			liburua1.setIzenburua("Harry Potter and the Philosopher's Stone");
			liburua1.setIdazlea(idazlea1);
			liburua1.setGeneroak(Arrays.asList("Fantasy", "Adventure"));
			
			Liburua liburua2 = new Liburua();
			liburua2.setIzenburua("A Game of Thrones");
			liburua2.setIdazlea(idazlea2);
			liburua2.setGeneroak(Arrays.asList("Fantasy", "Drama"));
			
			Liburua liburua3 = new Liburua();
			liburua3.setIzenburua("The Shining");
			liburua3.setIdazlea(idazlea3);
			liburua3.setGeneroak(Arrays.asList("Horror", "Thriller"));
			
			// Save books
			liburuRepo.save(liburua1);
			liburuRepo.save(liburua2);
			liburuRepo.save(liburua3);
			
			System.out.println("Database initialized with sample data!");
		};
	}
}
