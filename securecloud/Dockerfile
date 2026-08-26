FROM eclipse-temurin:26-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/securecloud-0.0.1-SNAPSHOT.jar"]
