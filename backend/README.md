# Breakable Toy I - To Do List App (Back-end)

This corresponds to the back-end portion of the full stack application. An API that consists of the following endpoints:

- A GET endpoint (/todos) to list “to do’s”
  - Include pagination. Pages should be of 10 elements.
  - Sort by priority and/or due date
  - Filter by done/undone
  - Filter by the name or part of the name
  - Filter by priority
- A POST endpoint (/todos) to create “to do’s”
  - Validations included
- A PUT endpoint (/todos/{id}) to update the “to do” name, due date and/or priority
  - Validations included
- A POST endpoint (/todos/{id}/done) to mark “to do” as done
  - This should update the “done date” property
  - If “to do” is already done nothing should happen (no error returned)
- A PUT endpoint (/todos/{id}/undone) to mark “to do” as undone
  - If “to do” is already undone nothing should happen
  - If “to do” is done, this should clear the done date
- GET endpoint (/todos/metrics) to retrieve the average completion time metrics.

## Technologies

- [Java](https://www.java.com/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [JUnit5](https://junit.org/)
- [Mockito](https://site.mockito.org/)
- [Maven](https://maven.apache.org/)

## How to run the project

```
# To run the back-end application:
mvn spring-boot:run
# To run all tests in the back-end application:
mvn test
```

Back-end project must run in port 9090.
