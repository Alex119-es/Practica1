package com.torneo_tenis.cache;

import com.torneo_tenis.model.Tenista1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación de caché FIFO (First In, First Out) para tenistas
 * Usa LinkedHashMap para mantener el orden de inserción
 */
public class TenistaCache {
    private static final Logger logger = LoggerFactory.getLogger(TenistaCache.class);
    
    private final int maxSize;
    private final LinkedHashMap<Long, Tenista1> cache;

    /**
     * Constructor con tamaño máximo por defecto
     */
    public TenistaCache() {
        this(10); // Tamaño por defecto
    }

    /**
     * Constructor con tamaño máximo personalizado
     */
    public TenistaCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("El tamaño máximo debe ser mayor que 0");
        }
        
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>();
        
        logger.info("Caché FIFO inicializado con tamaño máximo: " + maxSize);
    }

    /**
     * Obtiene un tenista del caché
     */
    public Optional<Tenista1> get(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        
        Tenista1 tenista = cache.get(id);
        if (tenista != null) {
            logger.debug("Cache HIT para ID: " + id);
            return Optional.of(tenista);
        } else {
            logger.debug("Cache MISS para ID: " + id);
            return Optional.empty();
        }
    }

    /**
     * Almacena un tenista en el caché
     */
    public void put(Long id, Tenista1 tenista) {
        if (id == null || tenista == null) {
            logger.warn("Intento de almacenar en caché con ID o tenista nulo");
            return;
        }
        
        // Si ya existe, lo actualizamos
        if (cache.containsKey(id)) {
            cache.put(id, tenista);
            logger.debug("Tenista actualizado en caché: ID=" + id + ", Nombre=" + tenista.getNombre());
            return;
        }
        
        // Si el caché está lleno, eliminamos el más antiguo (FIFO)
        if (cache.size() >= maxSize) {
            // Obtener la primera entrada (más antigua)
            Long oldestKey = cache.keySet().iterator().next();
            cache.remove(oldestKey);
            logger.debug("Eliminando entrada más antigua del caché: " + oldestKey);
        }
        
        // Añadir el nuevo elemento
        cache.put(id, tenista);
        logger.debug("Tenista almacenado en caché: ID=" + id + ", Nombre=" + tenista.getNombre());
    }

    /**
     * Elimina un tenista del caché
     */
    public boolean remove(Long id) {
        if (id == null) {
            return false;
        }
        
        Tenista1 removed = cache.remove(id);
        if (removed != null) {
            logger.debug("Tenista eliminado del caché: ID=" + id);
            return true;
        }
        return false;
    }

    /**
     * Limpia todo el caché
     */
    public void clear() {
        cache.clear();
        logger.info("Caché limpiado completamente");
    }

    /**
     * Verifica si un tenista está en el caché
     */
    public boolean contains(Long id) {
        return id != null && cache.containsKey(id);
    }

    /**
     * Obtiene el tamaño actual del caché
     */
    public int size() {
        return cache.size();
    }

    /**
     * Verifica si el caché está vacío
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    /**
     * Verifica si el caché está lleno
     */
    public boolean isFull() {
        return cache.size() >= maxSize;
    }

    /**
     * Obtiene el tamaño máximo del caché
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Obtiene información del estado del caché
     */
    public String getStats() {
        return String.format("Caché FIFO - Tamaño: %d/%d, Vacío: %b, Lleno: %b", 
                           size(), maxSize, isEmpty(), isFull());
    }

    /**
     * Actualiza un tenista en el caché (si existe)
     */
    public boolean update(Long id, Tenista1 tenista) {
        if (id == null || tenista == null) {
            return false;
        }
        
        if (cache.containsKey(id)) {
            cache.put(id, tenista);
            logger.debug("Tenista actualizado en caché: ID=" + id);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "TenistaCache{" +
                "maxSize=" + maxSize +
                ", currentSize=" + cache.size() +
                ", isEmpty=" + isEmpty() +
                ", isFull=" + isFull() +
                '}';
    }
}