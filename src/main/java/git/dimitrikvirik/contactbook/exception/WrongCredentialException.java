package git.dimitrikvirik.contactbook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WrongCredentialException extends ResponseStatusException {
    public WrongCredentialException() {
        super(HttpStatus.UNAUTHORIZED, "Wrong credentials");
    }
}
