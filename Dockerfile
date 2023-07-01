# Use the official OpenJDK 17 image as the base image
FROM openjdk:17-jdk-slim
# Set the working directory
WORKDIR /app
# Copy the JAR file built by your build system (e.g., Maven or Gradle) into the container
COPY target/crypto-1.0.0.jar /app/crypto-app.jar

COPY prices/BTC_values.csv /app/prices/BTC_values.csv
COPY prices/BTC_values2.csv /app/prices/BTC_values2.csv
COPY prices/ddd.csv /app/prices/ddd.csv
COPY prices/DOGE_values.csv /app/prices/DOGE_values.csv
COPY prices/ETH_values.csv /app/prices/ETH_values.csv
COPY prices/LTC_values.csv /app/prices/LTC_values.csv
COPY prices/XRP_values.csv /app/prices/XRP_values.csv

# Expose the port your Spring Boot app is running on
EXPOSE 8080
# Define the entrypoint command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "crypto-app.jar"]
