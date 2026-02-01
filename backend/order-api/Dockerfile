# ======= BUILD STAGE =======
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos todo el proyecto (incluye mvnw, pom.xml, src, etc.)
COPY . .

# Compilamos el jar (sin tests para ir rápido en deploy)
RUN mvn -q -DskipTests package


# ======= RUNTIME STAGE =======
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el jar generado en la fase anterior
COPY --from=build /app/target/*.jar app.jar

# Render suele exponer el puerto vía $PORT, y tu app usará ${PORT:8080}
EXPOSE 8080

# Arranque del servicio
ENTRYPOINT ["java","-jar","app.jar"]
