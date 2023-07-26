package git.dimitrikvirik.contactbook.service;

import git.dimitrikvirik.contactbook.exception.ResourceNotFoundException;
import git.dimitrikvirik.contactbook.model.entity.ContactBookEntity;
import git.dimitrikvirik.contactbook.model.param.ContactBookSearchParam;
import git.dimitrikvirik.contactbook.repository.ContactBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactBookService {

    private final ContactBookRepository contactBookRepository;

    private final MongoTemplate mongoTemplate;

    public ContactBookEntity save(ContactBookEntity contactBookEntity) {
        return contactBookRepository.save(contactBookEntity);
    }

    public Page<ContactBookEntity> findAll(ContactBookSearchParam searchParam, String ownerUserId, Pageable pageable) {
        //build mongo query
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(searchParam.firstname())) {
            sb.append(searchParam.firstname()).append(" ");
        }
        if (StringUtils.hasText(searchParam.lastname())) {
            sb.append(searchParam.lastname()).append(" ");
        }
        if (StringUtils.hasText(searchParam.address())) {
            sb.append(searchParam.address()).append(" ");
        }
        if (StringUtils.hasText(searchParam.phone())) {
            sb.append(searchParam.phone()).append(" ");
        }


        if (sb.length() != 0) {
            TextCriteria criteria = TextCriteria.forDefaultLanguage().caseSensitive(false).matching(sb.toString());
            Query query = TextQuery.queryText(criteria).sortByScore().with(pageable);
            query.addCriteria(Criteria.where("ownerUserId").is(ownerUserId));
            long count = mongoTemplate.count(query, ContactBookEntity.class);
            return new PageImpl<>(mongoTemplate.find(query, ContactBookEntity.class), pageable, count);
        }
        return contactBookRepository.findAllByOwnerUserId(ownerUserId, pageable);
    }

    public ContactBookEntity findById(String id) {
        return contactBookRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Contact book with id %s  not found", id)
        );
    }

    public void delete(ContactBookEntity contactBook) {
        contactBookRepository.delete(contactBook);
    }
}
