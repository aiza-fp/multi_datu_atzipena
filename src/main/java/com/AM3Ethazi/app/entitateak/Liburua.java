package com.AM3Ethazi.app.entitateak;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    
    @ElementCollection
    @CollectionTable(name = "liburua_generoak", joinColumns = @JoinColumn(name = "liburua_id"))
    @Column(name = "generoa")
    private List<String> generoak;
    
    
    
    @JsonProperty("idazle_izena")
    public String getIdazleIzena() {
        if (idazlea != null) {
            return idazlea.getIzena();
        } else {
            return null;
        }
    }

    
}