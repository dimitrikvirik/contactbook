package git.dimitrikvirik.contactbook.model.dto;

import java.time.LocalDateTime;

public record ErrorDTO(String message, int code, String status, LocalDateTime timestamp) {
}
