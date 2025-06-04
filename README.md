# Task API

## Exception Handling

This project uses centralized exception handling with Spring's `@ControllerAdvice`.

- `GlobalExceptionHandler` captures validation errors, resource not found, database, and generic exceptions.
- `ResourceNotFoundException` is thrown when an entity doesn't exist.
- Clean JSON error response format is returned to the client.

### Example Error Response

```json
{
  "timestamp": "2025-05-28T10:00:00",
  "status": 404,
  "message": "Task not found with id: ..."
}
```


## Swagger UI

After starting the application, access the API docs here:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
