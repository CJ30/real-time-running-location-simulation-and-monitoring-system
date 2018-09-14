#! /bin/bash
cd running-location-service
mvn clean install
java -jar ./target/*.jar

cd ../supply-location-service
mvn clean install
java -jar ./target/*.jar

cd ../spring-cloud-nike-running
mvn clean install
cd platform/eureka
java -jar ./target/*.jar
cd ../hystrix-dashboard
java -jar ./target/*.jar
cd ../../edging-service
java -jar ./target/*.jar

cd ../running-location-simulator
java -jar ./target/*.jar
cd ../running-location-distribution
java -jar ./target/*.jar
cd ../running-location-updater
java -jar ./target/*.jar