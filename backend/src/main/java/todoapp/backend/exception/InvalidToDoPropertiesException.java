package todoapp.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid ToDo's properties")
public class InvalidToDoPropertiesException extends RuntimeException {
    private String defaultMessage = "Invalid ToDo's properties. Please, make sure to input the data correctly.";

    @Getter
    private String message;

    public InvalidToDoPropertiesException() {
        this.message = defaultMessage;
    }

    public InvalidToDoPropertiesException(String msg) {
        super(msg);
        this.message = msg;
    }
}
