package git.dimitrikvirik.contactbook.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "contact_books")
@Builder
@Data
public class ContactBookEntity {

    @Id
    private String id;

    private String ownerUserId;

    @TextIndexed
    private String firstname;

    @TextIndexed
    private String lastname;

    @TextIndexed
    private String address;

    @TextIndexed
    private String phone;

    @TextIndexed
    private String email;


}
