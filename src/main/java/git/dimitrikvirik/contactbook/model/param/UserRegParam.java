package git.dimitrikvirik.contactbook.model.param;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record UserRegParam(
        @Length(min = 3)
        @NotBlank
        String username,
        @Length(min = 6)
        @NotBlank
        String password) {
}