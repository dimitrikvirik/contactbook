package git.dimitrikvirik.contactbook.model.dto;

import lombok.Builder;

@Builder
public record UserDTO(String id,
                      String username) { }
