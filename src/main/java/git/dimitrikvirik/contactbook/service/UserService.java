package git.dimitrikvirik.contactbook.service;

import git.dimitrikvirik.contactbook.exception.ResourceNotFoundException;
import git.dimitrikvirik.contactbook.model.entity.UserEntity;
import git.dimitrikvirik.contactbook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService  {
    private final UserRepository userRepository;


    public boolean existByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    public UserEntity findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username %s not found", username)));
    }

    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    public UserEntity findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id %s not found", id));
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

}
