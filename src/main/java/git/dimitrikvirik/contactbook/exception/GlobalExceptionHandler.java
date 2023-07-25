package git.dimitrikvirik.contactbook.exception;

import git.dimitrikvirik.contactbook.model.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDTO> handleResponseStatusException(ResponseStatusException e) {
        HttpStatusCode statusCode = e.getStatusCode();
        return getBody(e.getReason(), statusCode);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleException(Exception e) {
        HttpStatusCode statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        return getBody(e.getMessage(), statusCode);
    }

    private ResponseEntity<ErrorDTO> getBody(String msg, HttpStatusCode statusCode) {
        return ResponseEntity.status(statusCode).body(new ErrorDTO(msg, statusCode.value(), statusCode.toString(), LocalDateTime.now()));
    }
}
