# --- ETAPA 1: CONSTRUCCIÓN (BUILD) ---
# Usamos una imagen de Maven para compilar el proyecto
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copiamos todo el código fuente del proyecto al contenedor
COPY . .

# Ejecutamos el comando para generar el .jar (saltando los tests para ir más rápido)
RUN mvn clean package -DskipTests

# --- ETAPA 2: EJECUCIÓN (RUN) ---
# Usamos la imagen ligera para correr la app (igual que antes)
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp

# En lugar de copiar desde tu carpeta local, copiamos desde la "Etapa 1"
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8090

# Comando de arranque
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "/app.jar"]
