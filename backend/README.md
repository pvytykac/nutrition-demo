# Nutrition Demo Backend

## Overview
Spring Boot REST API for managing nutrition data.

## Project Description
REST APIs allowing users to manage lists of ingredients and recipes with the goal of keeping track of their daily nutrition details. 
The backend can also provide next-meal suggestions to users based on the meals they had already logged for the day and their nutrition goals.
We keep track of daily intake of fats, carbs and protein, including individual amino acids, especially phenylalanine (PKU).

## Package structure

Flat package structure nested under the root package: `net.pvytykac.nutrition`
Each module gets its own package, all the entity, repository, service, controller and representation classes live in this one package.
The visibility of classes is package private by default, only the classes that are required in other packages/modules are public - typically this would only be representation classes, in some circumstances service classes.
Global beans, shared helpers and generic reusable classes are all nested under the `net.pvytykac.nutrition.common` package.

## Technology Stack
- Java 17
- Spring Boot 3
- JPA + Hibernate
- PostgreSQL 18
- Liquibase for database migrations

## Technologies used
* PostgreSQL 18 for persistence
* Hibernate + JPA as ORM
* Liquibase for database migrations
* Spring events with JPA persistence to implement cross module functionality without coupling everything together
* Docker compose - there's a `compose.yaml` file that declares all external services used by the project, e.g. PostgreSQL container

## Running
```bash
./mvnw spring-boot:run
```

## Testing
* Repository classes - all custom repository methods get tested using the `@DataJpaTest` test slice. These tests run against a PostgreSQL testcontainer. `TestEntityManager` is used to setup test data. Liquibase data migrations are disabled, instead test schema is created using Hibernate by overriding the ddl setting.
* Service classes - standard unit test, covering all lines and branches, including error cases like exceptions raised
* Controller - all HTTP methods tested using the `@WebMvcTest` test slice, the service layer is mocked. The tests cover payload validation, response codes including error codes, response payloads.
* Response/Request representation classes - get tested to verify that JSON gets properly (de)serialized
* All the other classes get unit tested just like service classes

## REST APIs
Resource names are plural and the paths are versioned, there's no root context path. 
Example: in case of the "ingredient" resource the path would be `/v1/ingredients(/{ingredientId})`

Methods:
* `POST /resources` - creates a new resource and responds with 201 Created with the representation of the created resource in the response payload on success. On error, responds with 400 Bad Request if the request payload was invalid, or with 409 if the same resource already exists. When specified, query parameters will be used to filter resources matching the request. 
* `GET /resources` - provides paging (default page size 20), the response payload always being an object with "content" field containing the list of items
* `GET /resources/{resourceId}` - provides representation of a single resource if it exists, responds with 404 otherwise
* `PUT /resources/{resourceId}` - fully updates the resource to match the provided representation if the resource exists, responds with 404 otherwise
* `DELETE /resources/{resourceId}` - deletes the resource if it exists and responds with 204 No Content, returns 404 otherwise

## Query Parameter Filtering
* String - query parameters - `field.value` - string to use for filtering, `field.operator` - one of (EQUALS, STARTS_WITH, ENDS_WITH, CONTAINS)
* Enums - query parameters - `field.value` - set of enum items to use for filtering, `field.operator` - one of (IN, NOT_IN)
* Numbers - query parameters - `field.value` - set of numbers to use for filtering, `field.operator` - one of (EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUAL, LOWER_THAN, LOWER_THAN_OR_EQUAL, BETWEEN)

## Entity IDs
All JPA entity identifiers use **UUID** format (e.g., `550e8400-e29b-41d4-a716-446655440000`).
