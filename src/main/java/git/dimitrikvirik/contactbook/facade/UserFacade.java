package git.dimitrikvirik.contactbook.facade;

import git.dimitrikvirik.contactbook.exception.WrongCredentialException;
import git.dimitrikvirik.contactbook.mapper.UserMapper;
import git.dimitrikvirik.contactbook.model.dto.TokenDTO;
import git.dimitrikvirik.contactbook.model.dto.UserDTO;
import git.dimitrikvirik.contactbook.model.entity.UserEntity;
import git.dimitrikvirik.contactbook.model.enums.UserScope;
import git.dimitrikvirik.contactbook.model.param.UserLoginParam;
import git.dimitrikvirik.contactbook.model.param.UserRegParam;
import git.dimitrikvirik.contactbook.service.UserService;
import git.dimitrikvirik.contactbook.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;


    public UserDTO createUser(UserRegParam userParam) {
        if(userService.existByUsername(userParam.username())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("User with username %s already exist", userParam.username()));
        }
        UserEntity entity = UserMapper.toEntity(userParam, passwordEncoder);
        entity.setScopes(List.of(UserScope.CONTACT_BOOK_WRITE, UserScope.CONTACT_BOOK_READ));
        return UserMapper.toDTO(userService.save(entity));
    }

    public TokenDTO login(UserLoginParam userLoginParam) {
        UserEntity userEntity = userService.findByUsername(userLoginParam.username());
        if (!passwordEncoder.matches(userLoginParam.password(), userEntity.getPassword())) {
            throw new WrongCredentialException();
        }

        Claims claims = Jwts.claims();

        claims.put("username", userEntity.getUsername());
        claims.put("scopes", userEntity.getScopes().stream().map(Enum::name).toList());

        String token = jwtTokenUtil.doGenerateToken(claims, userEntity.getId());
        return new TokenDTO(token);
    }

    public UserDTO getUser(String id) {
        return UserMapper.toDTO(userService.findById(id));
    }
}
