**Desarrollado por**: Giovanni Alejandro Quishpe Chiliquinga

# 🎾 Torneo de Tenis - Aplicación de Gestión

## 📋 Descripción del Proyecto

Aplicación de consola en Java que gestiona un torneo de tenis mediante lectura de archivos CSV, almacenamiento en base de datos H2, consultas con API de colecciones y exportación en múltiples formatos (CSV, JSON, XML).

### Funcionalidades principales:
- ✅ Lectura y validación de datos desde CSV
- ✅ Almacenamiento en base de datos H2
- ✅ Caché FIFO con capacidad de 5 elementos
- ✅ Consultas estadísticas con API de colecciones funcional
- ✅ Exportación a CSV, JSON y XML
- ✅ Sistema completo de logging
- ✅ Validación exhaustiva de datos

## 🏗️ Arquitectura Usada

### Patrón de Capas (Layered Architecture)
```
┌─────────────────┐
│   Presentación  │ ← Main.java (Consola)
├─────────────────┤
│     Servicio    │ ← Lógica de negocio y consultas
├─────────────────┤
│   Repositorio   │ ← Acceso a datos (TenistaRepository)
├─────────────────┤
│     Modelo      │ ← Entidades (Tenista1, Mano)
└─────────────────┘
```

### Componentes principales:
- **Modelo**: `Tenista1`, `Mano`
- **Repositorio**: `ITenistaRepository`, `TenistaRepository`
- **Validación**: `TenistaValidator`
- **Caché**: `TenistaCache` (FIFO)
- **E/S**: `CsvReader`, `ITenistaWriter` + implementaciones
- **Configuración**: `DatabaseManager`

## 🔧 Principios SOLID Implementados

### 1. **S**ingle Responsibility Principle (SRP)
Cada clase tiene una única responsabilidad:

**Ejemplo 1: TenistaValidator**
```java
public class TenistaValidator {
    // Solo se encarga de validar datos de tenistas
    public static List<String> validar(Tenista1 tenista) {
        // Lógica de validación únicamente
    }
}
```

**Ejemplo 2: CsvReader**
```java
public class CsvReader {
    // Solo se encarga de leer archivos CSV
    public static List<Tenista1> leerTenistas(String filePath) {
        // Lógica de lectura CSV únicamente
    }
}
```

### 2. **O**pen/Closed Principle (OCP)
**Ejemplo 1: ITenistaWriter**
```java
public interface ITenistaWriter {
    void escribir(List<Tenista1> tenistas, String filePath) throws IOException;
}
// Abierto para extensión: CsvWriter, JsonWriter, XmlWriter
// Cerrado para modificación: no cambio la interfaz
```

**Ejemplo 2: Extensibilidad de formatos**
```java
// Fácil agregar nuevos formatos sin modificar código existente
ITenistaWriter writer = switch (extension) {
    case "csv" -> new CsvWriter();
    case "json" -> new JsonWriter();
    case "xml" -> new XmlWriter();
    // case "yaml" -> new YamlWriter(); // Futuro
};
```

### 3. **L**iskov Substitution Principle (LSP)
**Ejemplo 1: Implementaciones de ITenistaWriter**
```java
// Todas las implementaciones son intercambiables
ITenistaWriter csvWriter = new CsvWriter();
ITenistaWriter jsonWriter = new JsonWriter();
ITenistaWriter xmlWriter = new XmlWriter();
// Cualquiera funciona igual en el contexto
```

### 4. **I**nterface Segregation Principle (ISP)
**Ejemplo 1: Interfaces específicas**
```java
// Interfaz específica para repositorio
public interface ITenistaRepository {
    // Solo métodos relacionados con persistencia
}

// Interfaz específica para escritores
public interface ITenistaWriter {
    // Solo métodos relacionados con escritura
}
```

### 5. **D**ependency Inversion Principle (DIP)
**Ejemplo 1: Main depende de abstracciones**
```java
// Main depende de la interfaz, no de implementaciones concretas
ITenistaWriter writer = switch (extension) {
    case "csv" -> new CsvWriter();
    case "json" -> new JsonWriter();
    case "xml" -> new XmlWriter();
};
```

## 📚 Justificación de Librerías Usadas

### **H2 Database (v2.2.224)**
- **Uso**: Base de datos embebida
- **Justificación**: Ligera, rápida, ideal para aplicaciones standalone. No requiere instalación externa.

### **SLF4J (v2.0.9) + Logback (v1.4.11)**
- **Uso**: Sistema de logging
- **Justificación**: 
  - SLF4J: API estándar de logging, permite cambiar implementación
  - Logback: Implementación eficiente, configuración flexible

### **JUnit Jupiter (v5.10.0)**
- **Uso**: Testing unitario
- **Justificación**: Framework de testing moderno, soporte para Java 17+

### **Maven Shade Plugin (v3.4.1)**
- **Uso**: Creación de JAR ejecutable
- **Justificación**: Permite empaquetar todas las dependencias en un solo JAR

## 🚀 Uso de la Aplicación

### Compilación
```bash
mvn clean package
```

### Ejecución
```bash
# Con archivo de salida por defecto (torneo_tenis.json)
java -jar target/torneo_tenis.jar fichero_entrada.csv

# Con archivo de salida específico
java -jar target/torneo_tenis.jar fichero_entrada.csv salida.json
java -jar target/torneo_tenis.jar fichero_entrada.csv salida.csv  
java -jar target/torneo_tenis.jar fichero_entrada.csv salida.xml
```

## 📊 Consultas Implementadas

1. Tenistas ordenados por ranking (puntos desc)
2. Media de altura y peso
3. Tenista más alto
4. Tenistas de España
5. Agrupación por país
6. Número de tenistas por país ordenados por puntos
7. Agrupación por mano dominante con puntuación media
8. Puntuación total por país
9. País con mayor puntuación
10. Mejor tenista español

## 🗂️ Estructura del Proyecto

```
torneo_tenis/
├── pom.xml
├── fichero_entrada.csv
├── README.md
├── src/main/java/com/torneo_tenis/
│   ├── Main.java
│   ├── cache/TenistaCache.java
│   ├── io/
│   ├── model/
│   ├── repository/
│   └── validator/
├── src/main/resources/
│   ├── application.properties
│   └── logback.xml
├── data/
│   └── tenis_db.mv.db
├── log/
│   └── application.log
└── target/
    └── torneo_tenis.jar
```

---
**Desarrollado por**: [Tu Nombre]  
**Curso**: DWES 2024-2025  
**Java Version**: 17+  
**Build Tool**: Maven 3.8+