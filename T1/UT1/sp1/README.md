# Dockerizar una aplicación Spring Boot

## Limpiar jar
```bash
 ./gradlew clean bootJar
```

## Levantar contenedor
```bash
docker build -t dockerize-spring-boot .
docker run -it -p 8080:8080 --rm dockerize-spring-boot
```

## Pruebas
```bash
$ curl http://localhost:8080/ping
> Pong!
```