# REST, GraphQL және дерек форматтарын салыстыру

Java 17 + Spring Boot демо жобасы. Бір `Student` моделі REST және GraphQL арқылы беріледі; JSON Schema тексеруі және JSON/XML/YAML/Protobuf көлемі мен кодтау уақыты өлшенеді.

## Іске қосу

```bash
mvn spring-boot:run
```

## Endpoint-тер

- `GET http://localhost:8080/api/student` — REST арқылы студентті JSON түрінде алу.
- `POST http://localhost:8080/graphql` — GraphQL сұрауы: `query { student(id: 1) { id name email age } }`.
- `GET http://localhost:8080/api/benchmark` — форматтардың байт көлемі және 5000 рет кодтаудың уақыты.
- `POST http://localhost:8080/api/validate` — JSON денесін `student.schema.json` схемасымен тексеру.

Валидация мысалы:

```json
{"id":1,"name":"Aruzhan","email":"aruzhan@example.kz","age":20}
```

Өлшеу нәтижесі нақты компьютерге, JVM қызуына және кітапхана нұсқаларына тәуелді. Benchmark бір нысанды қайталап кодтайды; бұл жалпы желілік өнімділікті өлшемейді. Protobuf байттары тапсырмаға сай қарапайым wire encoding арқылы шығарылады.
