package git.dimitrikvirik.contactbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFoundException extends ResponseStatusException {
    public ResourceNotFoundException(String reason, Object... args) {
        super(HttpStatus.NOT_FOUND, String.format(reason, args));
    }
}
