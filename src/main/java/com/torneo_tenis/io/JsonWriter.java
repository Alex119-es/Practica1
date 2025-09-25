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
 * Escritor de tenistas en formato JSON
 */
public class JsonWriter implements ITenistaWriter {
    private static final Logger logger = LoggerFactory.getLogger(JsonWriter.class);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @Override
    public void escribir(List<Tenista1> tenistas, String filePath) throws IOException {
        logger.info("Escribiendo " + tenistas.size() + " tenistas a archivo JSON: " + filePath);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("{\n");
            writer.write("  \"tenistas\": [\n");
            
            for (int i = 0; i < tenistas.size(); i++) {
                writer.write(formatearTenistaJSON(tenistas.get(i), i == tenistas.size() - 1));
            }
            
            writer.write("  ]\n");
            writer.write("}\n");
            
            writer.flush();
        }
        
        logger.info("Archivo JSON escrito correctamente: " + filePath);
    }
    
    /**
     * Formatea un tenista como objeto JSON
     */
    private String formatearTenistaJSON(Tenista1 tenista, boolean esUltimo) {
        StringBuilder json = new StringBuilder();
        json.append("    {\n");
        json.append("      \"id\": ").append(tenista.getId()).append(",\n");
        json.append("      \"nombre\": \"").append(escaparJSON(tenista.getNombre())).append("\",\n");
        json.append("      \"pais\": \"").append(escaparJSON(tenista.getPais())).append("\",\n");
        json.append("      \"altura\": ").append(tenista.getAltura()).append(",\n");
        json.append("      \"peso\": ").append(tenista.getPeso()).append(",\n");
        json.append("      \"puntos\": ").append(tenista.getPuntos()).append(",\n");
        json.append("      \"mano\": \"").append(tenista.getMano().name()).append("\",\n");
        json.append("      \"fecha_nacimiento\": \"").append(tenista.getFecha_nacimiento().format(DATE_FORMATTER)).append("\",\n");
        json.append("      \"created_at\": \"").append(tenista.getCreated_at().format(DATETIME_FORMATTER)).append("\",\n");
        json.append("      \"updated_at\": \"").append(tenista.getUpdated_at().format(DATETIME_FORMATTER)).append("\"\n");
        json.append("    }");
        
        if (!esUltimo) {
            json.append(",");
        }
        
        json.append("\n");
        return json.toString();
    }
    
    /**
     * Escapa caracteres especiales para JSON
     */
    private String escaparJSON(String valor) {
        if (valor == null) {
            return "";
        }
        
        return valor.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}