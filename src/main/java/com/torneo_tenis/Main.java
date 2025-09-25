package com.torneo_tenis;

import com.torneo_tenis.cache.TenistaCache;
import com.torneo_tenis.io.*;
import com.torneo_tenis.model.Mano;
import com.torneo_tenis.model.Tenista1;
import com.torneo_tenis.repository.TenistaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        logger.info("🎾 Aplicación Torneo de Tenis iniciada");
        
        // Validar argumentos
        if (args.length == 0 || args.length > 2) {
            mostrarAyuda();
            return;
        }
        
        String archivoEntrada = args[0];
        String archivoSalida = args.length == 2 ? args[1] : "torneo_tenis.json";
        
        try {
            // Inicializar componentes
            TenistaRepository repository = new TenistaRepository();
            TenistaCache cache = new TenistaCache(5); // Tamaño de caché: 5
            
            logger.info("📂 Archivo de entrada: " + archivoEntrada);
            logger.info("📤 Archivo de salida: " + archivoSalida);
            
            // 1. Limpiar base de datos
            logger.info("🧹 Limpiando base de datos...");
            repository.deleteAll();
            
            // 2. Leer CSV y cargar en base de datos
            logger.info("📖 Leyendo archivo CSV...");
            List<Tenista1> tenistasCSV = CsvReader.leerTenistas(archivoEntrada);
            
            logger.info("💾 Insertando " + tenistasCSV.size() + " tenistas en la base de datos...");
            List<Tenista1> tenistasDB = new ArrayList<>();
            
            for (Tenista1 tenista : tenistasCSV) {
                Tenista1 tenistaGuardado = repository.save(tenista);
                tenistasDB.add(tenistaGuardado);
                cache.put(tenistaGuardado.getId(), tenistaGuardado);
                logger.debug("✅ Tenista guardado: " + tenistaGuardado.getNombre());
            }
            
            // 3. Mostrar consultas con API de colecciones
            logger.info("📊 Ejecutando consultas con API de colecciones...");
            mostrarConsultas(tenistasDB);
            
            // 4. Generar archivo de salida
            logger.info("📝 Generando archivo de salida...");
            generarArchivoSalida(tenistasDB, archivoSalida);
            
            // 5. Mostrar estadísticas finales
            mostrarEstadisticasFinales(repository, cache);
            
            // Cerrar conexiones
            repository.close();
            logger.info("🏁 Aplicación finalizada exitosamente");
            
        } catch (IOException e) {
            logger.error("❌ Error de E/S: " + e.getMessage(), e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            logger.error("❌ Error inesperado: " + e.getMessage(), e);
            System.err.println("Error inesperado: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Muestra todas las consultas requeridas usando API de colecciones
     */
    private static void mostrarConsultas(List<Tenista1> tenistas) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 CONSULTAS CON API DE COLECCIONES");
        System.out.println("=".repeat(60));
        
        // 1. Tenistas ordenados por ranking (puntos de mayor a menor)
        System.out.println("\n1️⃣ Tenistas ordenados por ranking (puntos desc):");
        tenistas.stream()
                .sorted((t1, t2) -> Integer.compare(t2.getPuntos(), t1.getPuntos()))
                .forEach(t -> System.out.printf("   %s - %d puntos%n", t.getNombre(), t.getPuntos()));
        
        // 2. Media de altura
        double mediaAltura = tenistas.stream()
                .mapToInt(Tenista1::getAltura)
                .average()
                .orElse(0.0);
        System.out.printf("\n2️⃣ Media de altura: %.2f cm%n", mediaAltura);
        
        // 3. Media de peso
        double mediaPeso = tenistas.stream()
                .mapToInt(Tenista1::getPeso)
                .average()
                .orElse(0.0);
        System.out.printf("\n3️⃣ Media de peso: %.2f kg%n", mediaPeso);
        
        // 4. Tenista más alto
        Optional<Tenista1> masAlto = tenistas.stream()
                .max(Comparator.comparing(Tenista1::getAltura));
        System.out.printf("\n4️⃣ Tenista más alto: %s (%d cm)%n", 
                masAlto.map(Tenista1::getNombre).orElse("N/A"),
                masAlto.map(Tenista1::getAltura).orElse(0));
        
        // 5. Tenistas de España
        System.out.println("\n5️⃣ Tenistas de España:");
        tenistas.stream()
                .filter(t -> t.getPais().equalsIgnoreCase("España"))
                .forEach(t -> System.out.printf("   %s%n", t.getNombre()));
        
        // 6. Tenistas agrupados por país
        System.out.println("\n6️⃣ Tenistas agrupados por país:");
        Map<String, List<Tenista1>> porPais = tenistas.stream()
                .collect(Collectors.groupingBy(Tenista1::getPais));
        porPais.forEach((pais, lista) -> {
            System.out.printf("   %s:%n", pais);
            lista.forEach(t -> System.out.printf("     - %s%n", t.getNombre()));
        });
        
        // 7. Número de tenistas agrupados por país y ordenados por puntos desc
        System.out.println("\n7️⃣ Número de tenistas por país (ordenado por puntos desc):");
        porPais.entrySet().stream()
                .sorted((e1, e2) -> {
                    int puntosE1 = e1.getValue().stream().mapToInt(Tenista1::getPuntos).sum();
                    int puntosE2 = e2.getValue().stream().mapToInt(Tenista1::getPuntos).sum();
                    return Integer.compare(puntosE2, puntosE1);
                })
                .forEach(entry -> {
                    int totalPuntos = entry.getValue().stream().mapToInt(Tenista1::getPuntos).sum();
                    System.out.printf("   %s: %d tenistas (%d puntos total)%n", 
                            entry.getKey(), entry.getValue().size(), totalPuntos);
                });
        
        // 8. Número de tenistas agrupados por mano dominante y puntuación media
        System.out.println("\n8️⃣ Tenistas por mano dominante y puntuación media:");
        Map<Mano, List<Tenista1>> porMano = tenistas.stream()
                .collect(Collectors.groupingBy(Tenista1::getMano));
        porMano.forEach((mano, lista) -> {
            double mediaPuntos = lista.stream().mapToInt(Tenista1::getPuntos).average().orElse(0.0);
            System.out.printf("   %s: %d tenistas (%.2f puntos promedio)%n", 
                    mano.name(), lista.size(), mediaPuntos);
        });
        
        // 9. Puntuación total agrupada por país
        System.out.println("\n9️⃣ Puntuación total por país:");
        Map<String, Integer> puntosPorPais = tenistas.stream()
                .collect(Collectors.groupingBy(
                        Tenista1::getPais,
                        Collectors.summingInt(Tenista1::getPuntos)
                ));
        puntosPorPais.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("   %s: %d puntos%n", 
                        entry.getKey(), entry.getValue()));
        
        // 10. País con más puntuación total
        Optional<Map.Entry<String, Integer>> paisConMasPuntos = puntosPorPais.entrySet().stream()
                .max(Map.Entry.comparingByValue());
        System.out.printf("\n🔟 País con más puntuación total: %s (%d puntos)%n",
                paisConMasPuntos.map(Map.Entry::getKey).orElse("N/A"),
                paisConMasPuntos.map(Map.Entry::getValue).orElse(0));
        
        // 11. Tenista con mejor ranking de España
        Optional<Tenista1> mejorEspanol = tenistas.stream()
                .filter(t -> t.getPais().equalsIgnoreCase("España"))
                .max(Comparator.comparing(Tenista1::getPuntos));
        System.out.printf("\n1️⃣1️⃣ Mejor tenista español: %s (%d puntos)%n",
                mejorEspanol.map(Tenista1::getNombre).orElse("N/A"),
                mejorEspanol.map(Tenista1::getPuntos).orElse(0));
        
        System.out.println("\n" + "=".repeat(60));
    }
    
    /**
     * Genera el archivo de salida según la extensión
     */
    private static void generarArchivoSalida(List<Tenista1> tenistas, String archivoSalida) throws IOException {
        String extension = obtenerExtension(archivoSalida).toLowerCase();
        
        ITenistaWriter writer = switch (extension) {
            case "csv" -> new CsvWriter();
            case "json" -> new JsonWriter();
            case "xml" -> new XmlWriter();
            default -> {
                logger.warn("Extensión no reconocida: " + extension + ". Usando JSON por defecto.");
                yield new JsonWriter();
            }
        };
        
        writer.escribir(tenistas, archivoSalida);
        System.out.printf("\n📤 Archivo generado: %s (%d tenistas)%n", archivoSalida, tenistas.size());
    }
    
    /**
     * Obtiene la extensión de un archivo
     */
    private static String obtenerExtension(String archivo) {
        int lastDot = archivo.lastIndexOf('.');
        return lastDot > 0 ? archivo.substring(lastDot + 1) : "json";
    }
    
    /**
     * Muestra estadísticas finales
     */
    private static void mostrarEstadisticasFinales(TenistaRepository repository, TenistaCache cache) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("📈 ESTADÍSTICAS FINALES");
        System.out.println("=".repeat(40));
        System.out.printf("💾 Tenistas en BD: %d%n", repository.count());
        System.out.printf("🗃️ Elementos en caché: %s%n", cache.getStats());
        System.out.println("=".repeat(40));
    }
    
    /**
     * Muestra la ayuda de uso
     */
    private static void mostrarAyuda() {
        System.out.println("\n🎾 TORNEO DE TENIS");
        System.out.println("=".repeat(40));
        System.out.println("Uso: java -jar torneo_tenis.jar <archivo_entrada.csv> [archivo_salida]");
        System.out.println("\nParámetros:");
        System.out.println("  archivo_entrada.csv  - Archivo CSV con datos de tenistas (OBLIGATORIO)");
        System.out.println("  archivo_salida       - Archivo de salida (.csv, .json, .xml)");
        System.out.println("                        Por defecto: torneo_tenis.json");
        System.out.println("\nEjemplos:");
        System.out.println("  java -jar torneo_tenis.jar tenistas.csv");
        System.out.println("  java -jar torneo_tenis.jar tenistas.csv salida.json");
        System.out.println("  java -jar torneo_tenis.jar tenistas.csv salida.xml");
        System.out.println("=".repeat(40));
    }
}