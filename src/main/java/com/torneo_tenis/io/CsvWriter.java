package com.torneo_tenis.io;

import com.torneo_tenis.model.Tenista1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Escritor de tenistas en formato CSV
 */
public class CsvWriter implements ITenistaWriter {
    private static final Logger logger = LoggerFactory.getLogger(CsvWriter.class);
    
    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_HEADER = "id,nombre,pais,altura,peso,puntos,mano,fecha_nacimiento,created_at,updated_at";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @Override
    public void escribir(List<Tenista1> tenistas, String filePath) throws IOException {
        logger.info("Escribiendo " + tenistas.size() + " tenistas a archivo CSV: " + filePath);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Escribir header
            writer.write(CSV_HEADER);
            writer.newLine();
            
            // Escribir cada tenista
            for (Tenista1 tenista : tenistas) {
                writer.write(formatearTenistaCSV(tenista));
                writer.newLine();
            }
            
            writer.flush();
        }
        
        logger.info("Archivo CSV escrito correctamente: " + filePath);
    }
    
    /**
     * Formatea un tenista como línea CSV
     */
    private String formatearTenistaCSV(Tenista1 tenista) {
        return String.join(CSV_SEPARATOR,
            String.valueOf(tenista.getId()),
            escaparCSV(tenista.getNombre()),
            escaparCSV(tenista.getPais()),
            String.valueOf(tenista.getAltura()),
            String.valueOf(tenista.getPeso()),
            String.valueOf(tenista.getPuntos()),
            tenista.getMano().name(),
            tenista.getFecha_nacimiento().format(DATE_FORMATTER),
            tenista.getCreated_at().format(DATETIME_FORMATTER),
            tenista.getUpdated_at().format(DATETIME_FORMATTER)
        );
    }
    
    /**
     * Escapa valores CSV (añade comillas si contiene comas)
     */
    private String escaparCSV(String valor) {
        if (valor == null) {
            return "";
        }
        
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        
        return valor;
    }
}