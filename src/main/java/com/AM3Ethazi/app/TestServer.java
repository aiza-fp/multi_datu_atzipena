package com.AM3Ethazi.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.AM3Ethazi.app.entitateak.Idazlea;
import com.AM3Ethazi.app.entitateak.Liburua;
import com.AM3Ethazi.app.repository.IdazleaRepository;
import com.AM3Ethazi.app.repository.LiburuaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Socket-based test server that provides the same data as the REST API
 * 
 * This server connects to the same database as the ApiController and handles text-based
 * socket requests, returning JSON responses identical to the REST endpoints.
 * Uses Jackson ObjectMapper for JSON serialization, which natively handles circular references.
 * 
 * To enable: Set testserver.enabled=true in application.properties
 * Server runs on port 8080 (configurable via PORT constant)
 * 
 * Supported commands:
 * - LIBURUAK / GET_BOOKS / /API/LIBURUAK → Returns all books (same as GET /api/liburuak)
 * - IDAZLEAK / GET_AUTHORS / /API/IDAZLEAK → Returns all authors (same as GET /api/idazleak)  
 * - TEST / /API/TEST → Returns "API is working!" (same as GET /api/test)
 * - INSERT_IDAZLEA:AuthorName → Adds new author to database
 * - INSERT_LIBURUA:BookTitle:AuthorId:Genre1,Genre2 → Adds new book to database
 * 
 * Usage example:
 * 1. Set testserver.enabled=true in application.properties
 * 2. Start the Spring Boot application
 * 3. Connect to localhost:8080 via socket
 * 4. Send text commands like "GET_BOOKS" to get JSON responses
 */
@Component
@Order(2) // Execute after database initialization (Order 1)
public class TestServer implements CommandLineRunner {
    private static final int PORT = 8080;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${testserver.enabled:false}")
    private boolean testServerEnabled;
    
    @Autowired
    private IdazleaRepository idazleaRepository;
    
    @Autowired
    private LiburuaRepository liburuaRepository;
    
    // Static reference to access repositories from static methods
    private static TestServer instance;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== TestServer CommandLineRunner starting ===");
        
        // Set static instance for access from static methods
        instance = this;
        
        // Only start the server if enabled
        if (testServerEnabled) {
            System.out.println("TestServer is enabled. Starting socket server...");
            startServer();
        } else {
            System.out.println("TestServer is disabled. Set testserver.enabled=true to enable it.");
        }
    }
    
    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Test server started on port " + PORT);
            System.out.println("Waiting for client connections...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                
                // Handle client in a new thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
            
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Transactional methods to handle database operations with proper session management
    @Transactional(readOnly = true)
    public List<Liburua> getAllLiburuak() {
        return liburuaRepository.findAll();
    }
    
    @Transactional(readOnly = true) 
    public List<Idazlea> getAllIdazleak() {
        // Now uses eager loading - no custom query needed!
        return idazleaRepository.findAll();
    }
    
    @Transactional
    public Idazlea saveIdazlea(String authorName) {
        Idazlea newIdazlea = new Idazlea();
        newIdazlea.setIzena(authorName);
        newIdazlea.setLiburuak(new ArrayList<>());
        return idazleaRepository.save(newIdazlea);
    }
    
    @Transactional
    public Liburua saveLiburua(String bookTitle, Long authorId, List<String> bookGenres) {
        java.util.Optional<Idazlea> authorOpt = idazleaRepository.findById(authorId);
        
        if (authorOpt.isPresent()) {
            Idazlea author = authorOpt.get();
            
            Liburua newLiburua = new Liburua();
            newLiburua.setIzenburua(bookTitle);
            newLiburua.setIdazlea(author);
            newLiburua.setGeneroak(bookGenres);
            
            return liburuaRepository.save(newLiburua);
        } else {
            throw new RuntimeException("Author not found with ID: " + authorId);
        }
    }
    
    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            String request = in.readLine();
            System.out.println("Received request: " + request);
            
            if (instance == null) {
                System.err.println("ERROR: TestServer instance is null!");
                out.println("ERROR: Server not properly initialized");
                return;
            }
            
            try {
                String response = instance.processRequest(request);
                System.out.println("Sending response: " + response);
                out.println(response);
            } catch (Exception e) {
                System.err.println("Exception in processRequest: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
                out.println("ERROR: " + e.getMessage());
            }
            
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
    

    
    @Transactional
    public String processRequest(String request) {
        if (request == null || request.isEmpty()) {
            return "ERROR: Empty request";
        }
        
        // Handle text-based commands that match ApiController endpoints
        String[] parts = request.split(":");
        String command = parts[0];

        try {
            switch (command) {
                case "GET_BOOKS":
                case "/API/LIBURUAK":
                case "LIBURUAK":
                    System.out.println("Accessing liburuak endpoint via text message");
                    List<Liburua> liburuak = getAllLiburuak();
                    System.out.println("Found " + liburuak.size() + " books");
                    return objectMapper.writeValueAsString(liburuak);
                    
                case "GET_AUTHORS":
                case "GET_AUTHOR_DATA":
                case "/API/IDAZLEAK":
                case "IDAZLEAK":
                    System.out.println("Accessing idazleak endpoint via text message");
                    List<Idazlea> idazleak = getAllIdazleak();
                    System.out.println("Found " + idazleak.size() + " authors");
                    return objectMapper.writeValueAsString(idazleak);
                    
                case "TEST":
                case "/API/TEST":
                    return "API is working!";
                    
                // Additional functionality for inserting new data
                case "INSERT_IDAZLEA":
                    // Format: INSERT_IDAZLEA:AuthorName
                    if (parts.length > 1) {
                        String authorName = parts[1];
                        
                        Idazlea savedIdazlea = saveIdazlea(authorName);
                        
                        System.out.println("Author inserted: " + authorName + " with ID: " + savedIdazlea.getId());
                        return "SUCCESS: Author inserted with ID " + savedIdazlea.getId();
                    }
                    return "ERROR: Missing author name. Format: INSERT_IDAZLEA:AuthorName";
                    
                case "INSERT_LIBURUA":
                    // Format: INSERT_LIBURUA:BookTitle:AuthorId:Genre1,Genre2
                    if (parts.length > 2) {
                        String bookTitle = parts[1];
                        Long authorId = Long.parseLong(parts[2]);
                        
                        // Get genres if provided
                        List<String> bookGenres = new ArrayList<>();
                        if (parts.length > 3 && !parts[3].isEmpty()) {
                            String[] genreArray = parts[3].split(",");
                            for (String genre : genreArray) {
                                if (!genre.trim().isEmpty()) {
                                    bookGenres.add(genre.trim());
                                }
                            }
                        }
                        
                        try {
                            Liburua savedLiburua = saveLiburua(bookTitle, authorId, bookGenres);
                            System.out.println("Book inserted: " + bookTitle + " (ID: " + savedLiburua.getId() + ") with genres: " + bookGenres);
                            return "SUCCESS: Book inserted with ID " + savedLiburua.getId();
                        } catch (RuntimeException e) {
                            return "ERROR: " + e.getMessage();
                        }
                    }
                    return "ERROR: Missing book information. Format: INSERT_LIBURUA:BookTitle:AuthorId:Genre1,Genre2";
                    
                default:
                    return "ERROR: Unknown command: " + command + 
                           "\nAvailable commands: GET_BOOKS, GET_AUTHORS, LIBURUAK, IDAZLEAK, TEST, INSERT_IDAZLEA:name, INSERT_LIBURUA:title:authorId:genres";
            }
        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }
} 