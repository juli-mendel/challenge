# Decisiones

En el siguiente documento dejo registrado decisiones que tomé en el proyecto.

## Arquitectura

Voy a basarme en una arquitectura hexagonal (siguiendo clean architecture).

- Dominio: Como entidad, Transaction
- Aplicación
  - Casos de uso:
    - SaveTransaction: Crea o actualiza transacciones
    - GetByType: Consultar transacciones por su tipo
    - GetSum: Consultar la suma relacionada transitivamente por sus parentsId
  - TransactionRepository: una interfaz que define las operaciones que se puede realizar para obtener datos de las transacciones en el storage
  - Service: Acá se puede optar por tener un sólo servicio que implemente los 3 casos de uso (si crecen los casos de uso puede crecer mucho). También puede optarse por tener 1 servicio por caso de uso. Pero termina quedando mucho archivo con poco código.
- Infra
    - DB: Implementaremos TransactionRepository como TransactionInMemoryRepositoryImpl
      - TransactionInMemoryRepositoryImpl va a tener 3 tablas:
        - transactionsById: Dado el ID de una transacción, devuelve el objeto Transaction
        - transactionSumById: Esta decisión se explicará más adelante, pero tiene cacheado la suma de sus hijos conectados por parentId transitivamente.
        - transactionsByType: Dado un tipo de transacción, devuelve las transacciones que tienen ese tipo
      - Las operaciones serán getTransactionByType, getTransactionSumById, saveTransaction. El TransactionInMemoryRepositoryImpl será responsable de actualizar transactionSumById.
    - Controllers: Son quienes manejan los REST endpoints, se comunican con los servicios y responden a las peticiones HTTP
      - Mapper y DTO: 
          - TransactionMapper: Mapea la consulta a a RequestDTO o ResponseDto y viceversa
          - DTO: DTO que representa la entidad para ser usada en la capa de Infra (por ejemplo, si queremos devolver la entidad Transaction y tiene datos que no queremos que sean visibles, esto no iría en el TransactionDTO)
     

De esta manera podemos abstraer el dominio de la infraestructura.
Para la aplicación es agnóstica el tipo de "storage" que se usará. Podríamos cambiar de una base de datos en memoria a una base de datos relacional.

### GetSum: Dado un transactionId, obtengo la suma de todos los hijos conectados por parentId transitivamente.

Con respecto a la decisión de tener transactionSumById. Encontré una solución cuya complejidad es O(h) (h altura del arbol) para la actualización y O(1) para la consulta.
Si cada vez que se consulta GetSum se tiene que recorrer todas las transacciones, reconstruir el árbol y recién ahí consultar la suma es ineficiente a medida que crecen las transacciones.

Cuando se inserta una nueva transacción, los costos deben actualizarse. Para esto alcanza con estos casos:
1. Si la nueva transacción tiene padre, se actualiza la suma del padre.
2. Si el padre de la nueva transacción tiene padre, se sigue actualizando la suma transitivamente.

En caso de actualizarse una transacción, pueden darse los siguientes casos:
1. La transacción mantiene su parent_id pero actualiza el monto.
2. La transacción cambia el parent_id
3. La transacción deja en null el nuevo parent_id.

En el caso 1, es actualizar desde la transacción actualizada para arriba en el árbol los costos
En el caso 2, es restar la suma de la versión anterior de la transacción actualizada para arriba en el árbol, y luego sumar la nueva versión de la transacción actualizada para arriba en el árbol.
En el caso 3, es restar la suma de la versión anterior de la transacción actualizada para arriba en el árbol.

## Asunciones
1. Siempre que se actualiza una transacción se chequea que no haya ciclos
2. Amount puede ser positivo, negativo o cero.
3. Una transacción no puede tenerse a sí mismo como padre.
4. El padre de una transacción tiene que existir. De todas formas, si no existiera, sólo cambiaría el algoritmo de cálculo de la suma.
5. type puede ser el string vacío.
6. Transaction va a fallar si parentId es igual al transaction id. De todas formas, el parentId tiene que existir, por lo que no debería pasar en el negocio de que parentId y transaction id sean iguales.
7. Si el type no fue registrado aún, el getByType devuelve un array vacío.
8. Asumo que no habrá IDs negativos.

## Decisiones
1. existsById únicamente pregunta si existe la key en el mapa en memoria. Podría reemplazarse por obtener la transacción y ver que sea distinto de Optional.empty().
2. No estoy chequeando por null en el TransactionService pensando en que más adelante, el controller (el único punto de acceso al service) no va a permitir que se llame al servicio con null.
3. transactionIdsByType se implementa como un diccionario indexado por el tipo. En vez de contener una lista de transacciones, obtiene una lista de IDs. Al ser un HashSet tiene un HashMap por dentro indexado por IDs.
4. Dado que la API puede llegar a recibir varias llamadas, se pueden dar varios race conditions cuando se guarda una nueva transacción y en paralelo se está leyendo algún dato. Para evitar esto, decidí usar un sólo lock en la clase TransactionService. Entonces mientras haya un thread llamando a una de estas funciones, el resto de los threads deben esperar. Esto se podría hacer aún más eficiente utilizando lock.writeLock().lock()/lock.readLock().lock(). Pero para el proyecto no es necesario.
5. Agrego jakarta.validation-api por una cuestión de simplicidad, los mismos chequeos podrían hacerse a mano en el controller.
6. No estoy agregando distinción entre UPDATED y CREATED. Para saveTransaction devuelvo 200 como status code si se guardó la nueva transacción.
