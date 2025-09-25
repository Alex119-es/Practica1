package com.torneo_tenis.io;

import com.torneo_tenis.model.Tenista1;
import java.io.IOException;
import java.util.List;

/**
 * Interfaz para escritores de tenistas en diferentes formatos
 */
public interface ITenistaWriter {
    /**
     * Escribe una lista de tenistas a un archivo
     * 
     * @param tenistas Lista de tenistas a escribir
     * @param filePath Ruta del archivo de salida
     * @throws IOException Si hay problemas al escribir el archivo
     */
    void escribir(List<Tenista1> tenistas, String filePath) throws IOException;
}