# Mendel API

Backend que almacena transacciones y permite consultar su suma total y tipos.

## Requisitos
- Java 17
- Maven
- Docker (opcional)

## Arquitectura del proyecto

El proyecto está desarrollado en Java, utilizando Spring Boot como framework principal. La arquitectura busca seguir el patrón de Arquitectura Hexagonal y se organiza en capas para mantener una separación clara de responsabilidades.

- Dominio: 
  - Contiene la entidad Transaction que valida sus propios invariantes (id no negativo, campos obligatorios, `parent_id` distinto del id).
  - Se encuentra en el paquete `com.julian.backend.domain`.
- Aplicación
  - Cuenta con los casos de uso (`SaveTransaction`, `GetTransactionSum`, `GetTransactionIdsByType`).
  - Los mismos se implementan en `TransactionService`.
  - Además expone un puerto `TransactionRepository` que define los métodos que la infraestructura debe implementar.
  - Se encuentra en el paquete `com.julian.backend.application`.
- Infraestructura
  - Contiene el controller REST, con sus DTOs y el mapper.
  - Define el `GlobalExceptionHandler`.
  - Implementa el repositorio en memoria.
  - Se encuentra en el paquete `com.julian.backend.infrastructure`.

## Endpoints

El proyecto cuenta con tres endpoints. Para más detalle referirse a la sección [Swagger](#swagger):

### PUT /transactions/$transaction_id
```
body: { "amount":double,"type":string, (Opcional) "parent_id":long }
```

Actualiza o crea una nueva transacción.

### GET /transactions/types/$type

Devuelve una lista de ids de las transacciones que contengan un tipo

### GET /transactions/sum/$transaction_id

Devuelve la suma total de las transacciones conectadas transitivamente por su parent_id.

## Cómo ejecutarlo
Para ejecutar la aplicación se puede hacer descargando el proyecto, y desde el directorio raíz ejecutar el siguiente comando:
```bash
./mvnw spring-boot:run
```

Además se puede correr con Docker, para ello se debe tener instalado Docker y ejecutar el siguiente comando:
```bash
docker build -t challenge-mendel .
docker run -p 8080:8080 challenge-mendel
```

Una vez corriendo el proyecto, se puede acceder probar los endpoints con cURL:

```bash
curl -i -X PUT localhost:8080/transactions/10 \
     -H 'Content-Type: application/json' \
     -d '{"amount": 5000, "type": "cars"}'

curl -i -X PUT localhost:8080/transactions/11 \
     -H 'Content-Type: application/json' \
     -d '{"amount": 10000, "type": "shopping", "parent_id": 10}'

curl -i localhost:8080/transactions/types/cars

curl -i localhost:8080/transactions/sum/10
```

## Test

Para ejecutar los test se puede hacer descargando el proyecto, y desde el directorio raíz ejecutar el siguiente comando:
```bash
./mvnw test
```

## Swagger
Una vez el proyecto levantado, se puede acceder a la documentación de la API a través de http://localhost:8080/swagger-ui.html

## Información adicional

- Las decisiones que se tomaron en el desarrollo del proyecto se encuentran documentadas en el archivo `DECISIONS.md`.
