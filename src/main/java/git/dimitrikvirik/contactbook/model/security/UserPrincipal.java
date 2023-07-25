package git.dimitrikvirik.contactbook.model.security;

import java.security.Principal;



public record UserPrincipal(String id, String username) implements Principal {

    @Override
    public String getName() {
        return id;
    }


}
