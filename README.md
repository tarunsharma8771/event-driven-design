# Event-Driven Order Processing with RabbitMQ

This project demonstrates an event-driven microservice workflow using Java, Spring Boot, RabbitMQ, Maven, Docker, and Docker Compose.

The sample e-commerce flow is:

- An order is placed.
- Payment processing and inventory reservation happen independently.
- A notification is sent after payment is completed.

## Architecture Overview

Services communicate asynchronously through RabbitMQ exchanges and queues instead of direct REST calls.

```text
Client
  |
  | POST /orders
  v
Order Service
  |
  | orderPlaced
  v
orderExchange
  |
  +--> orderQueue -----> Payment Service
  |
  +--> inventoryQueue -> Inventory Service

Payment Service
  |
  | paymentProcessed
  v
paymentExchange
  |
  +--> paymentQueue --> Notification Service
```

## Architecture Diagram

![Architecture](docs/images/arch.png)

## Modules

```text
event-driven-design
+-- common-events
|   +-- Shared models, RabbitMQ topology constants, and RabbitTemplate config
+-- order-service
|   +-- Exposes POST /orders and publishes orderPlaced events
+-- payment-service
|   +-- Consumes order events and publishes paymentProcessed events
+-- inventory-service
|   +-- Consumes order events and reserves stock
+-- notification-service
|   +-- Consumes payment events and sends notifications
+-- docker-compose.yml
```

## Shared Code

The `common-events` module contains common code from the services.

It contains:

- `com.tks.common.model.Order`
- `com.tks.common.model.Payment`
- `com.tks.common.messaging.RabbitTopology`
- `com.tks.common.config.CommonRabbitConfig`

Each service depends on `common-events` and imports `CommonRabbitConfig`.

## RabbitMQ Components

### Exchanges

| Exchange | Purpose |
| --- | --- |
| `orderExchange` | Receives `orderPlaced` events |
| `paymentExchange` | Receives `paymentProcessed` events |

### Queues

| Queue | Consumed By |
| --- | --- |
| `orderQueue` | Payment Service |
| `inventoryQueue` | Inventory Service |
| `paymentQueue` | Notification Service |

### Routing Keys

| Routing Key | Description |
| --- | --- |
| `orderPlaced` | New order created |
| `paymentProcessed` | Payment completed |

## Tech Stack

- Java 21
- Spring Boot 3.2.x
- Spring AMQP
- RabbitMQ
- Maven
- Docker
- Docker Compose

## Running the Application

### Prerequisites

- Java 21
- Maven 3.9+
- Docker
- Docker Compose

### Build Locally

```bash
mvn clean package
```

This builds all Maven modules:

- `common-events`
- `order-service`
- `payment-service`
- `notification-service`
- `inventory-service`

### Run with Docker Compose

```bash
docker compose up --build
```

If your Docker installation uses the older standalone Compose command:

```bash
docker-compose up --build
```

You can also skip the local Maven build and run only:

```bash
docker compose up --build
```

Each service Dockerfile builds its service jar inside Docker using the Maven reactor. The `-am` Maven flag also builds required modules such as `common-events`.

## Containers

Docker Compose starts one container per service:

| Container | Host Port |
| --- | --- |
| `order-service` | `8081` |
| `payment-service` | `8082` |
| `notification-service` | `8083` |
| `inventory-service` | `8084` |
| `rabbitmq` | `5672`, `15672` |

RabbitMQ has a healthcheck. The application services wait for RabbitMQ to become healthy before starting.

## Configuration

RabbitMQ connection settings are provided through `docker-compose.yml` environment variables:

```yaml
SPRING_RABBITMQ_HOST: rabbitmq
SPRING_RABBITMQ_PORT: 5672
SPRING_RABBITMQ_USERNAME: guest
SPRING_RABBITMQ_PASSWORD: guest
```

## Test the Flow

Send an order request:

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"product":"Laptop","quantity":2}'
```

Expected response:

```text
Order placed with ID: <generated-order-id>
```

Expected logs:

```text
order-service         | Order received with ID: <order-id>
payment-service       | Initiating payment for order ID: <order-id>
inventory-service     | Reserving order with ID: <order-id>
payment-service       | Payment completed for order ID: <order-id>
notification-service  | Notification sent for order ID: <order-id> has status SUCCESS
```

## RabbitMQ Management UI

Open:

```text
http://localhost:15672
```

Default credentials:

```text
Username: guest
Password: guest
```

## Key Concepts Demonstrated

- Event-driven architecture
- Producer/consumer messaging
- Publish-subscribe routing
- Asynchronous service communication
- Loose coupling between services
- Parallel event processing
- Shared event contracts
- RabbitMQ exchanges, queues, bindings, and routing keys

## Future Enhancements

- Dead letter queues
- Retry mechanism
- Idempotency handling
- Shipping service
- Distributed tracing
- OpenTelemetry integration
- Persistent storage
- Saga pattern implementation

## License

This project is licensed under the MIT License.
