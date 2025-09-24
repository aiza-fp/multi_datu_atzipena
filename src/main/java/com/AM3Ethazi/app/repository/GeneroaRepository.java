package com.AM3Ethazi.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AM3Ethazi.app.entitateak.Generoa;

public interface GeneroaRepository extends JpaRepository<Generoa, Long> {

    Generoa findByIzena(String izena);
}
