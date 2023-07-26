package git.dimitrikvirik.contactbook.mapper;

import git.dimitrikvirik.contactbook.model.dto.ContactBookDTO;
import git.dimitrikvirik.contactbook.model.entity.ContactBookEntity;
import git.dimitrikvirik.contactbook.model.param.ContactBookParam;

public class ContactBookMapper {

    private ContactBookMapper() {
    }
    public static ContactBookDTO toDTO(ContactBookEntity entity) {
        return ContactBookDTO.builder()
                .id(entity.getId())
                .ownerUserId(entity.getOwnerUserId())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .build();
    }

    public static ContactBookEntity toEntity(ContactBookParam contactBookParam, String ownerUserId) {
        return ContactBookEntity.builder()
                .ownerUserId(ownerUserId)
                .firstname(contactBookParam.firstname())
                .lastname(contactBookParam.lastname())
                .phone(contactBookParam.phone())
                .address(contactBookParam.address())
                .email(contactBookParam.email())
                .build();
    }
}
