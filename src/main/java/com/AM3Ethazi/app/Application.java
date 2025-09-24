package com.AM3Ethazi.app;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

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
	@Order(1) // Execute first - initialize database before starting TestServer
	public CommandLineRunner initData(IdazleaRepository idazleaRepo, LiburuaRepository liburuRepo) {
		return args -> {
			System.out.println("=== Starting database initialization ===");
			
			// Check if data already exists
			long authorCount = idazleaRepo.count();
			long bookCount = liburuRepo.count();
			System.out.println("Current database state - Authors: " + authorCount + ", Books: " + bookCount);
			
			if (authorCount > 0 || bookCount > 0) {
				System.out.println("Database already contains data. Skipping initialization.");
				return;
			}
			
			// Create authors
			Idazlea idazlea1 = new Idazlea();
			idazlea1.setIzena("J.K. Rowling");
			
			Idazlea idazlea2 = new Idazlea();
			idazlea2.setIzena("George R.R. Martin");
			
			Idazlea idazlea3 = new Idazlea();
			idazlea3.setIzena("Stephen King");
			
			// Save authors
			System.out.println("Saving authors...");
			idazleaRepo.save(idazlea1);
			idazleaRepo.save(idazlea2);
			idazleaRepo.save(idazlea3);
			System.out.println("Authors saved successfully!");
			
			// Create books
			Liburua liburua1 = new Liburua();
			liburua1.setIzenburua("Harry Potter and the Philosopher's Stone");
			liburua1.setIdazlea(idazlea1);
			liburua1.setGeneroak(Arrays.asList("Fantasy", "Adventure"));

			Liburua liburua2 = new Liburua();
			liburua2.setIzenburua("Harry Potter and the Chamber of Secrets");
			liburua2.setIdazlea(idazlea1);
			liburua2.setGeneroak(Arrays.asList("Fantasy", "Mystery"));

			Liburua liburua3 = new Liburua();
			liburua3.setIzenburua("Harry Potter and the Prisoner of Azkaban");
			liburua3.setIdazlea(idazlea1);
			liburua3.setGeneroak(Arrays.asList("Fantasy", "Thriller"));

			Liburua liburua4 = new Liburua();
			liburua4.setIzenburua("A Game of Thrones");
			liburua4.setIdazlea(idazlea2);
			liburua4.setGeneroak(Arrays.asList("Fantasy", "Drama"));

			Liburua liburua5 = new Liburua();
			liburua5.setIzenburua("A Clash of Kings");
			liburua5.setIdazlea(idazlea2);
			liburua5.setGeneroak(Arrays.asList("Fantasy", "Epic"));

			Liburua liburua6 = new Liburua();
			liburua6.setIzenburua("A Storm of Swords");
			liburua6.setIdazlea(idazlea2);
			liburua6.setGeneroak(Arrays.asList("Fantasy", "Adventure"));

			Liburua liburua7 = new Liburua();
			liburua7.setIzenburua("The Shining");
			liburua7.setIdazlea(idazlea3);
			liburua7.setGeneroak(Arrays.asList("Horror", "Thriller"));

			Liburua liburua8 = new Liburua();
			liburua8.setIzenburua("It");
			liburua8.setIdazlea(idazlea3);
			liburua8.setGeneroak(Arrays.asList("Horror", "Supernatural"));

			Liburua liburua9 = new Liburua();
			liburua9.setIzenburua("Carrie");
			liburua9.setIdazlea(idazlea3);
			liburua9.setGeneroak(Arrays.asList("Horror", "Drama"));

			// Save books
			System.out.println("Saving books...");
			liburuRepo.save(liburua1);
			liburuRepo.save(liburua2);
			liburuRepo.save(liburua3);
			liburuRepo.save(liburua4);
			liburuRepo.save(liburua5);
			liburuRepo.save(liburua6);
			liburuRepo.save(liburua7);
			liburuRepo.save(liburua8);
			liburuRepo.save(liburua9);
			System.out.println("Books saved successfully!");
			
			// Final verification
			long finalAuthorCount = idazleaRepo.count();
			long finalBookCount = liburuRepo.count();
			System.out.println("=== Database initialization completed ===");
			System.out.println("Final database state - Authors: " + finalAuthorCount + ", Books: " + finalBookCount);
		};
	}
}
