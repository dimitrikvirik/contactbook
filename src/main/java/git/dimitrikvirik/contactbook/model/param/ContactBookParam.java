package git.dimitrikvirik.contactbook.model.param;

import lombok.Builder;

@Builder
public record ContactBookParam(String firstname,
                               String lastname,
                               String phone,
                               String email,
                               String address) {
}
