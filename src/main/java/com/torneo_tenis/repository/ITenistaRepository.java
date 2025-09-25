package com.torneo_tenis.repository;

import com.torneo_tenis.model.Tenista1;
import com.torneo_tenis.model.Mano;
import java.util.List;
import java.util.Optional;

public interface ITenistaRepository {
    List<Tenista1> findAll();
    Optional<Tenista1> findById(Long id);
    Tenista1 save(Tenista1 tenista);
    Tenista1 update(Tenista1 tenista);
    boolean deleteById(Long id);
    void deleteAll();
    
    // Consultas específicas
    List<Tenista1> findByPais(String pais);
    List<Tenista1> findByMano(Mano mano);
    List<Tenista1> findByPuntosGreaterThan(int puntos);
    long count();
}