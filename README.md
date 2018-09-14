# Real-time Running Location Simulation System
## Introduction:
Real-time Running Location Simulation System is mainly a backend system with light-weight frontend presentation. The goal is to provide a running assistant by simulating runner's running process, including changes of runner status, location, speed, headings, etc. and represents it in real time on the frontend page.

### Architecture:
Backend services are built in Microservices style and integrated in a Cloud Native way.

![](https://github.com/CJ30/real-time-running-location-simulation-system/blob/master/pics/runningArchitecture.png)


### Spring Cloud Running:
Spring Cloud Running is the core project of Real-time Running Location Simulation System, which consists of Edging Service, platform (Service Registration and Discovery, Circuit Breaker), Running Location Simulator, Running Location Distribution Service and Running Location Updater.


### Running Location Simulator:
* Built with: Spring Boot, Spring Cloud Netflix Eureka
When runner sends running simulation request, Simulator starts to simulate running process of that runner and eventually sends simulation result to Distribution Service. Since there would be multiple runners running at the same time, the simulation task is designed to execute on asynchronous task executor.

* **PathService**:
	When Location Simulator Rest Controller received the simulation request from client side, Rest Controller calls PathService to do preparation for simulation. Path Service converts mock data, "init-location.json", to SimulatorInitLocations (an object contains a list of GpsSimulatorRequest, getter method and setter method).

* **GpsSimulatorFactoryService**:
	Rest Controller continues to pass each GpsSimulatorRequest to GpsSimulatorFactoryService. Each GpsSimulatorRequest contains polyline (a string stores a series of coordinates by lossy compression algorithm), and GpsSimulatorFactoryService extracts data from request and initialize LocationSimulator.

* **AsyncTaskExecutor**:
	For better thread management, this LocationSimulator thread task is passed to AsyncTaskExecutor where this thread would be executed continuously until this thread is interrupted. A Future object is generated immediately which is wrapped into a LocationSimulatorInstance object and stored in a future Hashmap used for canceling thread.

* **NavUtils & PositionService**:
	During execution, NavUtils service is used to calculate next Point on the earth surface based on current position, heading and speed. Afterwards, updated position info is sent to Distribution Service via REST API.

![](https://github.com/CJ30/real-time-running-location-simulation-system/blob/master/pics/simulator.png)


### Running Location Distribution Service:
* Built with: Spring Boot, Spring Cloud Stream, RabbitMQ, Spring Cloud Netflix Eureka
Distribution Service receives REST Call from Simulator and convert the data format to Message, a form valid in RabbitMQ. And pass message to RabbitMQ, which runs in Docker container.


### Running Location Updater:
* Built with: Spring Boot, Spring Cloud Stream, RabbitMQ, WebSocket
Updater is the Sink of RabbitMQ and consumes messages from queue and also connects with frontend via WebSocket, sending the simulation result to frontend, which eventually present on the browser.


### Running Location Service:
* Built with: Spring Boot, Spring Data JPA, Spring Data REST
Running Location Service is an initialization service, initializing each runner's status in in-memory database (H2). Each running locations represents a runner and after initialization, runners could search data. For further refinement, mock data could be substituted with real data from active users, and add monitor service at backend which could locate each runner and get corresponding status data.


### Supply Location Service:
* Built with: Spring Boot, Spring Data REST, Spring Data MongoDB, MongoDB
Supply Location Service is an initialization service, initializing each supply location, providing materials and services, information. Since supply location information would not change often, this data is store in NoSQL database (MongoDB). 

Runner could send request of finding the nearest supply location to Supply Location Service, and the nearest location information would be sent back and runner could acquire supply.


### Service Registration and Discovery:
* Built with: Spring Boot, Spring Cloud Netflix Eureka
To extend this Microservices system in a Cloud Native way, Spring Cloud Netflix Eureka server is added. An Eureka Server is set up and all microservices mentioned are refactored as an Eureka client. 


### Circuit Breaker:
* Built with: Spring Boot, Spring Cloud Netflix Hystrix
To protect whole system due to one service failure, Spring Cloud Hystrix is added. A Hystrix Dashboard is set up. Refactor PositionService in Simulator, when REST request to Distribution Service fails, a Hystrix fallback method is triggered to handle the error to prevent the crush of all services.


### Routing & Filtering:
* Built with: Spring Boot, Spring Cloud Netflix Zuul
To solve CORs problems, integrate Edging Service by Spring Cloud Netflix Zuul. Take Running Location Service for example, since Running Location Service (port:9000) is added in configuration file of Edging server (port: 8080), Edging server could work as a router. Any request to http://localhost:9000/locations would be equal to http://localhost:8080/running-location-service/locations.


### Frontend page:
* Built with: Thymeleaf, JavaScript, HTML, CSS, Bootstrap
Thymeleaf template engine is used to generate page on the backend side and render it on frontend. Frontend is connected with backend via WebSocket.

On page, there're two buttons, Subscribe and Unsubscribe, which control if frontend subscribes the WebSocket channel.

When subscribes the channel, any simulation result would be presented to frontend in real time and if unsubscribes the channel, there would be no messages anymore.

When channel is connected, by click sendMessage button, frontend could send customized message to backend. 


## Getting Started:
```
sh ./launcher.sh
```


## Testing:
Simulation Test:
REST Client send Simulation request:
```
http://localhost:9005/api/simulation
```
Frontend presentation:
```
http://localhost:9007
```
Watch RabbitMQ data flow:
```
http://localhost:15672
```

## Author
* Chengjin Sun (CJ30)
