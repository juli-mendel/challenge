# Backend API

## Arquitectura del proyecto

El proyecto está desarrollado en Java 17, utilizando Spring Boot como framework principal. La arquitectura busca seguir el patrón de Arquitectura Hexagonal y se organiza en capas para mantener una separación clara de responsabilidades.

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
  - Implementa la base de datos en memoria.
  - Se encuentra en el paquete `com.julian.backend.infrastructure`.

## Endpoints

El proyecto cuenta con tres endpoints. Para más detalle referirse a la sección [Swagger](#swagger):

### PUT /transactions/$transaction_id
```
body: { "amount":double,"type":string,"parent_id":long }
```

Actualiza o crea una nueva transacción.

### GET /transactions/types/$type

Devuelve una lista de las transacciones que contengan un tipo

### GET /transactions/sum

Devuelve la suma total de las transacciones conectadas transitivamente por su parent_id.

## Como ejecutarlo
Para ejecutar la aplicación se puede hacer descargando el proyecto, y desde el directorio raíz ejecutar el siguiente comando:
```bash
./mvnw spring-boot:run
```

Además se puede correr con Docker, para ello se debe tener instalado Docker y ejecutar el siguiente comando:
```bash
docker build -t challenge-mendel .
docker run -p 8080:8080 challenge-mendel
```

## Swagger
Una vez el proyecto levantado, se puede acceder a la documentación de la API a través de la siguiente URL:
```bash
http://localhost:8080/swagger-ui.html
```

## Información adicional

- Las decisiones que se tomaron en el desarrollo del proyecto se encuentran documentadas en el archivo `DECISIONS.md`.
