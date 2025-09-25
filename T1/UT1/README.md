# Dockerizar una aplicación Spring Boot

En esta primera práctica lo que haremos es crear un proyecto Spring Boot de ejemplo muy sencillo para ver cómo funciona una aplicación dentro de un framework. No te preocupes por el código, lo veremos después más a fondo cómo funciona este conjunto de Utilidades de Spring llamado Spring Boot que agiliza la creación de aplicaciones empresariales.

1. Creamos un proyecto Spring Boot, para ello o bien tenemos el Ultimate o nos vamos a [Spring Initz](https://start.spring.io/index.html)

2. Creamos el controlador en la carpeta `src\main\java\org\example\sp1`:

```java
package org.example.sp1;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "Pong";
    }
}
```

3. Construimos y Ejecutamos

4. En caso de recrear el jar:
```bash
 ./gradlew clean bootJar
```

## Contenedores

1. Creamos dentro de la carpeta el contenedor y lo ejecutamos

```bash
docker build -t dockerize-spring-boot .
docker run -it -p 8080:8080 --rm dockerize-spring-boot
```

## Pruebas

1. En consola

```bash
$ curl http://localhost:8080/ping
> Pong!
```

2. Probaremos en POSTMAN
