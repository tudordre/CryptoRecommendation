# Crypto Recommendation service

# Stack

![](https://img.shields.io/badge/java_17-✓-blue.svg)
![](https://img.shields.io/badge/spring_boot-✓-blue.svg)
![](https://img.shields.io/badge/swagger_2-✓-blue.svg)
![](https://img.shields.io/badge/docker-✓-blue.svg)
![](https://img.shields.io/badge/kubernetes-✓-blue.svg)
![](https://img.shields.io/badge/junit-✓-blue.svg)
![](https://img.shields.io/badge/mockito-✓-blue.svg)

# Project description

This is a crypto recommendation service.
It retrieves crypto currencies prices from CVS files and creates reports based on this information.

Main features
-
- List all the crypto currencies and their normalized value.
- Detailed information for a specified crypto currency. The period is flexible from 1 month up to 120 months.
- Crypto currency with the highest normalized.
- Reads data from CVS. Supports multiple files for the same crypto currency.
- Supported crypto currencies stored in a property file. The ones present in the CSV but not present in the supported list won't be taken in account. In order to add a new one add it also in the property file. 
- Containerization with Docker and Kubernetes. Steps to run the service on Docker/Kubernetes in the file
- Rate limit the malicious users base on their ip. Uses a simple HashMap for storing the ip activities which works correctly for just one instance of the service.

# How to run this project locally?

1. Make sure you have [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) and [Maven](https://maven.apache.org) installed

2. Clone this repository

```
git clone https://github.com/ProjectorTeam/songprojector-be.git
```

3. Navigate into the folder

```
cd CryptoRecommendation
```

4. Install dependencies

```
mvn install
```

5. Run the project

```
mvn spring-boot:run
```

6. Navigate to `http://localhost:8080/swagger-ui.html` in your browser to check everything is working correctly. You can change the default port in the `application.yml` file

```
server:
  port: 8080
```

7. Make a GET request to `/prices` to see all the crypto currencies and their normalized value

```
curl --location 'localhost:8080/prices/btC?year=2022&month=1&period=1'
```

8. Make a GET request to `/prices/"{symbol}"` to see a more detailed information for a specified crypto currency

```
curl --location 'localhost:8080/prices/btC?year=2022&month=1&period=1'
```

9. Make a GET request to `/prices/highest` to see crypto currency with the highest normalized

```
curl --location 'localhost:8080/prices/highest?date=2022-01-01'```
