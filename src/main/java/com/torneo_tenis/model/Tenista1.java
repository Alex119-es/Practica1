package com.torneo_tenis.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clase que representa un tenista
 */
public class Tenista1 {
    private Long id;
    private String nombre;
    private String pais;
    private int altura;
    private int peso;
    private int puntos;
    private Mano mano;
    private LocalDate fecha_nacimiento;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    /**
     * Constructor por defecto
     */
    public Tenista1() {
    }

    /**
     * Constructor completo
     */
    public Tenista1(Long id, String nombre, String pais, int altura, int peso, 
                   int puntos, Mano mano, LocalDate fecha_nacimiento, 
                   LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.altura = altura;
        this.peso = peso;
        this.puntos = puntos;
        this.mano = mano;
        this.fecha_nacimiento = fecha_nacimiento;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    // GETTERS
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPais() {
        return pais;
    }

    public int getAltura() {
        return altura;
    }

    public int getPeso() {
        return peso;
    }

    public int getPuntos() {
        return puntos;
    }

    public Mano getMano() {
        return mano;
    }

    public LocalDate getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    // SETTERS
    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public void setMano(Mano mano) {
        this.mano = mano;
    }

    public void setFecha_nacimiento(LocalDate fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    // MÉTODOS ADICIONALES
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenista1 tenista1 = (Tenista1) o;
        return Objects.equals(id, tenista1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tenista1{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", pais='" + pais + '\'' +
                ", altura=" + altura +
                ", peso=" + peso +
                ", puntos=" + puntos +
                ", mano=" + mano +
                ", fecha_nacimiento=" + fecha_nacimiento +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }

    /**
     * Crea una copia del tenista
     */
    public Tenista1 copy() {
        return new Tenista1(id, nombre, pais, altura, peso, puntos, mano, 
                           fecha_nacimiento, created_at, updated_at);
    }

    /**
     * Actualiza las fechas de modificación
     */
    public void updateTimestamp() {
        this.updated_at = LocalDateTime.now();
    }
}