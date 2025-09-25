package com.torneo_tenis.validator;

import com.torneo_tenis.model.Tenista1;
import com.torneo_tenis.model.Mano;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TenistaValidator {
    private static final Logger logger = LoggerFactory.getLogger(TenistaValidator.class);
    
    // Constantes para validación
    private static final int MIN_ALTURA = 140;
    private static final int MAX_ALTURA = 230;
    private static final int MIN_PESO = 40;
    private static final int MAX_PESO = 150;
    private static final int MIN_PUNTOS = 0;
    private static final int MAX_PUNTOS = 20000;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Valida un tenista completo
     */
    public static List<String> validar(Tenista1 tenista) {
        List<String> errores = new ArrayList<>();
        
        errores.addAll(validarNombre(tenista.getNombre()));
        errores.addAll(validarPais(tenista.getPais()));
        errores.addAll(validarAltura(tenista.getAltura()));
        errores.addAll(validarPeso(tenista.getPeso()));
        errores.addAll(validarPuntos(tenista.getPuntos()));
        errores.addAll(validarMano(tenista.getMano()));
        errores.addAll(validarFechaNacimiento(tenista.getFecha_nacimiento()));
        
        if (errores.isEmpty()) {
            logger.debug("Tenista válido: " + tenista.getNombre());
        } else {
            logger.warn("Tenista inválido: " + tenista.getNombre() + ". Errores: " + errores);
        }
        
        return errores;
    }

    /**
     * Valida el nombre del tenista
     */
    public static List<String> validarNombre(String nombre) {
        List<String> errores = new ArrayList<>();
        
        if (nombre == null || nombre.trim().isEmpty()) {
            errores.add("El nombre no puede estar vacío");
        } else if (nombre.trim().length() < 2) {
            errores.add("El nombre debe tener al menos 2 caracteres");
        } else if (nombre.trim().length() > 100) {
            errores.add("El nombre no puede exceder 100 caracteres");
        } else if (!nombre.matches("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1\\s'-]+$")) {
            errores.add("El nombre solo puede contener letras, espacios, apostrofes y guiones");
        }
        
        return errores;
    }

    /**
     * Valida el país del tenista
     */
    public static List<String> validarPais(String pais) {
        List<String> errores = new ArrayList<>();
        
        if (pais == null || pais.trim().isEmpty()) {
            errores.add("El país no puede estar vacío");
        } else if (pais.trim().length() < 2) {
            errores.add("El país debe tener al menos 2 caracteres");
        } else if (pais.trim().length() > 50) {
            errores.add("El país no puede exceder 50 caracteres");
        } else if (!pais.matches("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1\\s'-]+$")) {
            errores.add("El país solo puede contener letras, espacios, apostrofes y guiones");
        }
        
        return errores;
    }

    /**
     * Valida la altura del tenista
     */
    public static List<String> validarAltura(int altura) {
        List<String> errores = new ArrayList<>();
        
        if (altura < MIN_ALTURA) {
            errores.add("La altura debe ser mayor a " + MIN_ALTURA + " cm");
        } else if (altura > MAX_ALTURA) {
            errores.add("La altura debe ser menor a " + MAX_ALTURA + " cm");
        }
        
        return errores;
    }

    /**
     * Valida el peso del tenista
     */
    public static List<String> validarPeso(int peso) {
        List<String> errores = new ArrayList<>();
        
        if (peso < MIN_PESO) {
            errores.add("El peso debe ser mayor a " + MIN_PESO + " kg");
        } else if (peso > MAX_PESO) {
            errores.add("El peso debe ser menor a " + MAX_PESO + " kg");
        }
        
        return errores;
    }

    /**
     * Valida los puntos del tenista
     */
    public static List<String> validarPuntos(int puntos) {
        List<String> errores = new ArrayList<>();
        
        if (puntos < MIN_PUNTOS) {
            errores.add("Los puntos no pueden ser negativos");
        } else if (puntos > MAX_PUNTOS) {
            errores.add("Los puntos no pueden exceder " + MAX_PUNTOS);
        }
        
        return errores;
    }

    /**
     * Valida la mano del tenista
     */
    public static List<String> validarMano(Mano mano) {
        List<String> errores = new ArrayList<>();
        
        if (mano == null) {
            errores.add("La mano no puede ser nula");
        }
        
        return errores;
    }

    /**
     * Valida la fecha de nacimiento del tenista
     */
    public static List<String> validarFechaNacimiento(LocalDate fechaNacimiento) {
        List<String> errores = new ArrayList<>();
        
        if (fechaNacimiento == null) {
            errores.add("La fecha de nacimiento no puede ser nula");
        } else {
            LocalDate hoy = LocalDate.now();
            LocalDate fechaMinima = hoy.minusYears(60);
            LocalDate fechaMaxima = hoy.minusYears(16);
            
            if (fechaNacimiento.isBefore(fechaMinima)) {
                errores.add("El tenista no puede tener más de 60 años");
            } else if (fechaNacimiento.isAfter(fechaMaxima)) {
                errores.add("El tenista debe tener al menos 16 años");
            }
        }
        
        return errores;
    }

    /**
     * Valida una cadena de fecha y la convierte a LocalDate
     */
    public static LocalDate validarYConvertirFecha(String fechaStr) throws DateTimeParseException {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            throw new DateTimeParseException("La fecha no puede estar vacía", fechaStr, 0);
        }
        
        try {
            return LocalDate.parse(fechaStr.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.error("Error al parsear fecha: " + fechaStr, e);
            throw new DateTimeParseException("Formato de fecha inválido. Use yyyy-MM-dd", fechaStr, 0);
        }
    }

    /**
     * Valida una cadena de mano y la convierte a enum Mano
     */
    public static Mano validarYConvertirMano(String manoStr) throws IllegalArgumentException {
        if (manoStr == null || manoStr.trim().isEmpty()) {
            throw new IllegalArgumentException("La mano no puede estar vacía");
        }
        
        String manoUpper = manoStr.trim().toUpperCase();
        
        // Mapear diferentes variantes a nuestros enum values
        switch (manoUpper) {
            case "DIESTRO":
            case "DERECHA":
            case "RIGHT":
                return Mano.DERECHA;
            case "ZURDO":
            case "IZQUIERDA":
            case "LEFT":
                return Mano.IZQUIERDA;
            default:
                throw new IllegalArgumentException("Mano inválida: " + manoStr + ". Use DIESTRO/ZURDO o DERECHA/IZQUIERDA");
        }
    }

    /**
     * Valida si un tenista es válido (sin errores)
     */
    public static boolean esValido(Tenista1 tenista) {
        return validar(tenista).isEmpty();
    }

    /**
     * Valida y arroja excepción si hay errores
     */
    public static void validarYArrojarExcepcion(Tenista1 tenista) throws ValidationException {
        List<String> errores = validar(tenista);
        if (!errores.isEmpty()) {
            throw new ValidationException("Errores de validación: " + String.join(", ", errores));
        }
    }

    /**
     * Excepción personalizada para errores de validación
     */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}