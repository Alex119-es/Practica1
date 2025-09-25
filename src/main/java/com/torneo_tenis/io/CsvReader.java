package com.torneo_tenis.io;

import com.torneo_tenis.model.Tenista1;
import com.torneo_tenis.model.Mano;
import com.torneo_tenis.validator.TenistaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lector de archivos CSV para tenistas
 */
public class CsvReader {
    private static final Logger logger = LoggerFactory.getLogger(CsvReader.class);
    
    private static final String CSV_SEPARATOR = ",";
    private static final int EXPECTED_COLUMNS = 7;

    /**
     * Lee tenistas desde un archivo CSV
     * 
     * @param filePath Ruta del archivo CSV
     * @return Lista de tenistas válidos
     * @throws IOException Si hay problemas al leer el archivo
     */
    public static List<Tenista1> leerTenistas(String filePath) throws IOException {
        logger.info("Iniciando lectura del archivo CSV: " + filePath);
        
        validateFilePath(filePath);
        
        List<Tenista1> tenistas = new ArrayList<>();
        List<String> erroresGlobales = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            
            if (headerLine == null) {
                throw new IOException("El archivo CSV está vacío");
            }
            
            validateHeader(headerLine);
            
            String line;
            int lineNumber = 2; // Empezamos desde la línea 2 (después del header)
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    logger.debug("Saltando línea vacía: " + lineNumber);
                    lineNumber++;
                    continue;
                }
                
                try {
                    Tenista1 tenista = parsearLineaTenista(line, lineNumber);
                    if (tenista != null) {
                        tenistas.add(tenista);
                        logger.debug("Tenista parseado correctamente: " + tenista.getNombre());
                    }
                } catch (Exception e) {
                    String error = "Error en línea " + lineNumber + ": " + e.getMessage();
                    erroresGlobales.add(error);
                    logger.error(error, e);
                }
                
                lineNumber++;
            }
        }
        
        if (!erroresGlobales.isEmpty()) {
            logger.warn("Se encontraron " + erroresGlobales.size() + " errores durante la lectura:");
            erroresGlobales.forEach(logger::warn);
        }
        
        logger.info("Lectura completada. Tenistas válidos: " + tenistas.size());
        return tenistas;
    }

    /**
     * Valida que la ruta del archivo sea correcta
     */
    private static void validateFilePath(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IOException("La ruta del archivo no puede estar vacía");
        }
        
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new IOException("El archivo no existe: " + filePath);
        }
        
        if (!Files.isReadable(path)) {
            throw new IOException("El archivo no es legible: " + filePath);
        }
        
        if (!filePath.toLowerCase().endsWith(".csv")) {
            throw new IOException("El archivo debe tener extensión .csv");
        }
        
        logger.debug("Archivo validado correctamente: " + filePath);
    }

    /**
     * Valida el header del CSV
     */
    private static void validateHeader(String headerLine) throws IOException {
        if (headerLine == null || headerLine.trim().isEmpty()) {
            throw new IOException("El header del CSV no puede estar vacío");
        }
        
        String[] headers = headerLine.split(CSV_SEPARATOR);
        
        if (headers.length != EXPECTED_COLUMNS) {
            throw new IOException("El header debe tener exactamente " + EXPECTED_COLUMNS + 
                                " columnas, pero tiene " + headers.length);
        }
        
        String[] expectedHeaders = {
            "nombre", "pais", "altura", "peso", "puntos", "mano", "fecha_nacimiento"
        };
        
        for (int i = 0; i < expectedHeaders.length; i++) {
            if (!headers[i].trim().equalsIgnoreCase(expectedHeaders[i])) {
                throw new IOException("Header incorrecto en columna " + (i + 1) + 
                                    ". Esperado: '" + expectedHeaders[i] + 
                                    "', Encontrado: '" + headers[i].trim() + "'");
            }
        }
        
        logger.debug("Header validado correctamente");
    }

    /**
     * Parsea una línea del CSV a un objeto Tenista
     */
    private static Tenista1 parsearLineaTenista(String line, int lineNumber) throws Exception {
        String[] campos = line.split(CSV_SEPARATOR);
        
        if (campos.length != EXPECTED_COLUMNS) {
            throw new IllegalArgumentException("La línea debe tener exactamente " + EXPECTED_COLUMNS + 
                                             " campos, pero tiene " + campos.length);
        }
        
        try {
            // Limpiar y extraer campos
            String nombre = campos[0].trim();
            String pais = campos[1].trim();
            String alturaStr = campos[2].trim();
            String pesoStr = campos[3].trim();
            String puntosStr = campos[4].trim();
            String manoStr = campos[5].trim();
            String fechaStr = campos[6].trim();
            
            // Validar y convertir datos
            int altura = parseInteger(alturaStr, "altura");
            int peso = parseInteger(pesoStr, "peso");
            int puntos = parseInteger(puntosStr, "puntos");
            
            LocalDate fechaNacimiento = TenistaValidator.validarYConvertirFecha(fechaStr);
            Mano mano = TenistaValidator.validarYConvertirMano(manoStr);
            
            LocalDateTime ahora = LocalDateTime.now();
            
            // Crear tenista
            Tenista1 tenista = new Tenista1(
                0L, // El ID se asignará en la base de datos
                nombre,
                pais,
                altura,
                peso,
                puntos,
                mano,
                fechaNacimiento,
                ahora,
                ahora
            );
            
            // Validar tenista completo
            List<String> errores = TenistaValidator.validar(tenista);
            if (!errores.isEmpty()) {
                throw new IllegalArgumentException("Datos inválidos: " + String.join(", ", errores));
            }
            
            return tenista;
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error al convertir número: " + e.getMessage());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error al convertir fecha: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error de validación: " + e.getMessage());
        }
    }

    /**
     * Parsea un string a entero con validación
     */
    private static int parseInteger(String value, String fieldName) throws NumberFormatException {
        if (value == null || value.isEmpty()) {
            throw new NumberFormatException("El campo '" + fieldName + "' no puede estar vacío");
        }
        
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("El campo '" + fieldName + "' debe ser un número entero válido: " + value);
        }
    }

    /**
     * Cuenta las líneas válidas de un archivo CSV (sin incluir header)
     */
    public static int contarLineasValidas(String filePath) throws IOException {
        validateFilePath(filePath);
        
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // Saltar header
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    count++;
                }
            }
        }
        
        logger.debug("Líneas válidas encontradas: " + count);
        return count;
    }
}