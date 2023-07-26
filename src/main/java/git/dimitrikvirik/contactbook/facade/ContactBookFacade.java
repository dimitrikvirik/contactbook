package git.dimitrikvirik.contactbook.facade;

import git.dimitrikvirik.contactbook.mapper.ContactBookMapper;
import git.dimitrikvirik.contactbook.model.dto.ContactBookDTO;
import git.dimitrikvirik.contactbook.model.entity.ContactBookEntity;
import git.dimitrikvirik.contactbook.model.param.ContactBookParam;
import git.dimitrikvirik.contactbook.model.param.ContactBookSearchParam;
import git.dimitrikvirik.contactbook.service.ContactBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ContactBookFacade {

    private final ContactBookService contactBookService;

    public ContactBookDTO addContactBook(ContactBookParam contactBookParam, Principal principal) {
        return ContactBookMapper.toDTO(contactBookService.save(ContactBookMapper.toEntity(contactBookParam, principal.getName())));
    }

    @PostAuthorize("returnObject.ownerUserId == authentication.name")
    public ContactBookDTO getContactBook(String id) {
        return ContactBookMapper.toDTO(contactBookService.findById(id));
    }

    public void deleteContactBook(String id, Principal principal) {
        ContactBookEntity contactBook = getContactBookEntity(id, principal);
        contactBookService.delete(contactBook);
    }


    public ContactBookDTO updateContactBook(String id, ContactBookParam contactBookDTO, Principal principal) {
        getContactBookEntity(id, principal);
        return ContactBookMapper.toDTO(contactBookService.save(ContactBookMapper.toEntity(contactBookDTO, principal.getName())));
    }

    private ContactBookEntity getContactBookEntity(String id, Principal principal) {
        ContactBookEntity contactBook = contactBookService.findById(id);
        if (!contactBook.getOwnerUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can delete only your own contact books");
        }
        return contactBook;
    }

    public Page<ContactBookDTO> getAllContactBooks(ContactBookSearchParam searchParam, Principal principal, Pageable pageable) {
        return contactBookService.findAll(searchParam, principal.getName(), pageable).map(ContactBookMapper::toDTO);
    }
}
