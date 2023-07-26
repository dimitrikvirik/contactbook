package git.dimitrikvirik.contactbook.mapper;

import git.dimitrikvirik.contactbook.model.dto.UserDTO;
import git.dimitrikvirik.contactbook.model.entity.UserEntity;
import git.dimitrikvirik.contactbook.model.param.UserRegParam;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserMapper {

    private UserMapper() {
    }

    public static UserEntity toEntity(UserRegParam userParam, PasswordEncoder passwordEncoder) {
        return UserEntity
                .builder()
                .username(userParam.username())
                .password(passwordEncoder.encode(userParam.password()))
                .build();
    }

    public static UserDTO toDTO(UserEntity entity) {
        return UserDTO
                .builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .build();
    }
}