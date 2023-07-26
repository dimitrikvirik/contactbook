package git.dimitrikvirik.contactbook.repository;

import git.dimitrikvirik.contactbook.model.entity.ContactBookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ContactBookRepository extends MongoRepository<ContactBookEntity, String> {

    Page<ContactBookEntity> findAllByOwnerUserId(String ownerUserId, Pageable pageable);


}
