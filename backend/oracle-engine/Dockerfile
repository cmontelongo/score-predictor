# Usamos la misma base
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copiamos el jar de este módulo
COPY target/*.jar app.jar

# Exponemos el puerto 8080 (internamente el contenedor usa el 8080)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]