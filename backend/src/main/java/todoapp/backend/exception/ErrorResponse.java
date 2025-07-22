package todoapp.backend.exception;

import java.util.Map;

import lombok.Data;

@Data
public class ErrorResponse {
    private String error;
    private String message;
    private Map<String, String> body;

    public ErrorResponse(String err, String msg) {
        this.error = err;
        this.message = msg;
        this.body = Map.of("error", err, "message", msg);
    }
}
