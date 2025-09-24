package com.AM3Ethazi.app.entitateak;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Liburua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String izenburua;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "idazlea_id")
    private Idazlea idazlea;
    
    @JsonIgnore //Original field: List<Generoa> generoak is hidden from JSON with @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "liburua_generoak",
        joinColumns = @JoinColumn(name = "liburua_id"),
        inverseJoinColumns = @JoinColumn(name = "generoa_id")
    )
    private List<Generoa> generoak;
    
    
    
    @JsonProperty("idazle_izena")
    public String getIdazleIzena() {
        if (idazlea != null) {
            return idazlea.getIzena();
        } else {
            return null;
        }
    }
    
    @JsonProperty("generoak") // Custom getter: getGeneroakAsList() is exposed as "generoak" in JSON
    public List<String> getGeneroakAsList() {
        if (generoak == null) {
            return List.of();
        }
        return generoak.stream()
                .map(Generoa::getIzena)
                .toList();
    }

    
}