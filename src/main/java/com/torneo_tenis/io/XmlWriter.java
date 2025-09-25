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
 * Escritor de tenistas en formato XML
 */
public class XmlWriter implements ITenistaWriter {
    private static final Logger logger = LoggerFactory.getLogger(XmlWriter.class);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @Override
    public void escribir(List<Tenista1> tenistas, String filePath) throws IOException {
        logger.info("Escribiendo " + tenistas.size() + " tenistas a archivo XML: " + filePath);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Escribir cabecera XML
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<tenistas>\n");
            
            // Escribir cada tenista
            for (Tenista1 tenista : tenistas) {
                writer.write(formatearTenistaXML(tenista));
            }
            
            // Cerrar elemento raíz
            writer.write("</tenistas>\n");
            
            writer.flush();
        }
        
        logger.info("Archivo XML escrito correctamente: " + filePath);
    }
    
    /**
     * Formatea un tenista como elemento XML
     */
    private String formatearTenistaXML(Tenista1 tenista) {
        StringBuilder xml = new StringBuilder();
        xml.append("  <tenista>\n");
        xml.append("    <id>").append(tenista.getId()).append("</id>\n");
        xml.append("    <nombre>").append(escaparXML(tenista.getNombre())).append("</nombre>\n");
        xml.append("    <pais>").append(escaparXML(tenista.getPais())).append("</pais>\n");
        xml.append("    <altura>").append(tenista.getAltura()).append("</altura>\n");
        xml.append("    <peso>").append(tenista.getPeso()).append("</peso>\n");
        xml.append("    <puntos>").append(tenista.getPuntos()).append("</puntos>\n");
        xml.append("    <mano>").append(tenista.getMano().name()).append("</mano>\n");
        xml.append("    <fecha_nacimiento>").append(tenista.getFecha_nacimiento().format(DATE_FORMATTER)).append("</fecha_nacimiento>\n");
        xml.append("    <created_at>").append(tenista.getCreated_at().format(DATETIME_FORMATTER)).append("</created_at>\n");
        xml.append("    <updated_at>").append(tenista.getUpdated_at().format(DATETIME_FORMATTER)).append("</updated_at>\n");
        xml.append("  </tenista>\n");
        return xml.toString();
    }
    
    /**
     * Escapa caracteres especiales para XML
     */
    private String escaparXML(String valor) {
        if (valor == null) {
            return "";
        }
        
        return valor.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}