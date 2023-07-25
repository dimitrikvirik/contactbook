package git.dimitrikvirik.contactbook;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@OpenAPIDefinition(info =
    @Info(
            title = "Contact Book API",
            version = "1.0",
            description = "Documentation Contact Book API v1.0")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class ContactBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactBookApplication.class, args);
    }

}
