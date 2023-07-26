package git.dimitrikvirik.contactbook.model.dto;

import lombok.Builder;

@Builder
public record ContactBookDTO(String id,
                             String ownerUserId,
                             String firstname,
                             String lastname,
                             String email,
                             String phone,
                             String address) {
}
