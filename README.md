
# Spring Boot + Kafka + Clickhouse

This is an example **Spring Boot + Apache Kafka + Clickhouse + kafdrop** app.

It was made using **Spring Boot**, **Clickhouse**, **Clickhouse Kafka Engine**, **Apache Kafka**, **Apache ZooKeeper**, **Spring Kafka**, **Docker**, **kafdrop** and **Docker Compose**.

## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)
- [Docker]()


### Clone

Run the command below to clone the project:


```sh
git clone https://github.com/keza/Spring-Boot-Clickhouse-Kafka-Engine.git
```

### Run

Build Docker image:

We are using **Docker-Compose** to start the containers. Go to the root folder where 'docker-compose.yml' is located and run the below command:

    cd clickhouse-client
    
    mvn clean install -DskipTests
    
    cd ..

    docker-compose up --build -d
    
*[Optional]* You can either open a separate terminal and follow the logs while systems are initializing:
```
docker-compose logs -f
```
*[Optional]* Or check the starting status:
```
docker-compose ps
```
    
### Create Event

```sh
docker exec -it clickhouse-project_kafka_1 /usr/bin/kafka-console-producer   --broker-list localhost:9092 --topic events

{"type": "post_view","post_id": 50}
{"type": "post_view","post_id": 50}
{"type": "post_view","post_id": 50}
```

### fetch data via clickhouse http interface

```sh
curl --location --request GET 'localhost:8090/events/post/http/50'
```

### fetch data via clickhouse-native-jdbc

```sh
curl --location --request GET 'localhost:8090/events/post/jdbc/50'
```

### Kafdrop – Kafka Web UI 

http://localhost:9005/

### Stop
Go to the root folder where is *docker-compose.yml*.

To stop all containers execute:
```
docker-compose down
```
