package todoapp.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No ToDo matching ID was found")
public class ToDoNotFoundException extends RuntimeException {
    @Getter
    private String message;

    public ToDoNotFoundException() {
    }

    public ToDoNotFoundException(String msg) {
        super(msg);
        this.message = msg;
    }
}
