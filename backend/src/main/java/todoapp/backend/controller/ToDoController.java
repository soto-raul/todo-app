package todoapp.backend.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import todoapp.backend.enums.Priority;
import todoapp.backend.enums.Status;
import todoapp.backend.exception.ErrorResponse;
import todoapp.backend.exception.InvalidToDoPropertiesException;
import todoapp.backend.exception.ToDoNotFoundException;
import todoapp.backend.model.FilterCriteria;
import todoapp.backend.model.ToDo;
import todoapp.backend.service.ToDoService;
import todoapp.backend.util.Validators;

@CrossOrigin("*")
@RestController
public class ToDoController {
    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    // API ENDPOINTS
    @GetMapping("/todos")
    public ResponseEntity<?> getAllToDos(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Status doneStatus,
            @RequestParam(required = true, defaultValue = "0") int page,
            @RequestParam(required = true, defaultValue = "10") int size) {

        Pageable pageReq = PageRequest.of(page, size);
        // Call getAll if there's no filters or getByCriteria if there's at least 1
        if (Validators.validateAllCriteriaAreNull(name, priority, doneStatus)) {
            return ResponseEntity.ok(toDoService.getAllToDos(pageReq));
        } else {
            return ResponseEntity
                    .ok(toDoService.getByCriteria(new FilterCriteria(name, priority, doneStatus), pageReq));
        }
    }

    @PostMapping("/todos")
    public ResponseEntity<ToDo> addToDo(@RequestBody ToDo toDo) {
        ToDo addedToDo = toDoService.addToDo(toDo);
        if (addedToDo == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(addedToDo);
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<ToDo> updateToDo(@RequestBody ToDo toDo, @PathVariable int id) {
        ToDo updatedToDo = toDoService.updateToDo(id, toDo);

        if (updatedToDo == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(updatedToDo);
    }

    @PutMapping("/todos/{id}/done")
    public ResponseEntity<ToDo> markAsDone(@PathVariable int id) {
        ToDo updatedToDo = toDoService.updateToDoDoneStatus(id, Status.DONE);

        if (updatedToDo == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(updatedToDo);
    }

    @PutMapping("/todos/{id}/undone")
    public ResponseEntity<ToDo> markAsNotDone(@PathVariable int id) {
        ToDo updatedToDo = toDoService.updateToDoDoneStatus(id, Status.NOT_DONE);

        if (updatedToDo == null) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

        return ResponseEntity.ok(updatedToDo);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<?> deleteToDo(@PathVariable int id) {
        if (!toDoService.deleteToDo(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // EXCEPTION HANDLERS
    @ExceptionHandler(value = InvalidToDoPropertiesException.class)
    public ResponseEntity<?> handleInvalidToDoPropertiesException(InvalidToDoPropertiesException ex) {
        ErrorResponse error = new ErrorResponse("INVALID_TODO_PROPERTIES", ex.getMessage());
        return new ResponseEntity<>(error.getBody(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ToDoNotFoundException.class)
    public ResponseEntity<?> handleToDoNotFoundException(ToDoNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error.getBody(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse("INVALID_TODO_PROPERTIES",
                "Some properties' types or format are invalid. Please, make sure they match their expected type and format");
        return new ResponseEntity<>(error.getBody(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorResponse error = new ErrorResponse("REQUEST_PARAMS_TYPE_MISMATCH",
                "Some request parameters' types are invalid. Please, make sure they match their expected type");
        return new ResponseEntity<>(error.getBody(), HttpStatus.BAD_REQUEST);
    }
}
