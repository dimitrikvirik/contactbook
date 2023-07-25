package git.dimitrikvirik.contactbook.model.entity;


import git.dimitrikvirik.contactbook.model.enums.UserScope;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
@Data
@Builder
public class UserEntity  {

    @Id
    private String id;


    private String username;

    private String password;

    private List<UserScope> scopes;


}
