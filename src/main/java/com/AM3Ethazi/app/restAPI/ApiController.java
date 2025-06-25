package com.AM3Ethazi.app.restAPI;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AM3Ethazi.app.entitateak.Idazlea;
import com.AM3Ethazi.app.entitateak.Liburua;
import com.AM3Ethazi.app.repository.IdazleaRepository;
import com.AM3Ethazi.app.repository.LiburuaRepository;


@RestController
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private IdazleaRepository idazleaRepo;
	
	@Autowired
	private LiburuaRepository liburuRepo;
	
	@GetMapping("/liburuak")
	public List<Liburua> getLiburuak() {	
		System.out.println("Accessing /api/liburuak endpoint");
		List<Liburua> liburuak = liburuRepo.findAll();
		System.out.println("Found " + liburuak.size() + " books");
		return liburuak;
	}
	
	@GetMapping("/idazleak")
	public List<Idazlea> getIdazleak() {	
		System.out.println("Accessing /api/idazleak endpoint");
		List<Idazlea> idazleak = idazleaRepo.findAll();
		System.out.println("Found " + idazleak.size() + " authors");
		return idazleak;
	}
	
	@GetMapping("/test")
	public String test() {
		return "API is working!";
	}
}
