# Usamos una imagen base de Java 17 (mismo que definimos en el pom.xml)
FROM eclipse-temurin:21-jdk-alpine

# Definimos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el JAR generado por Maven al contenedor
# Nota: Esto asume que ejecutaste 'mvn clean package' antes
COPY target/*.jar app.jar

# Exponemos el puerto (informativo)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]