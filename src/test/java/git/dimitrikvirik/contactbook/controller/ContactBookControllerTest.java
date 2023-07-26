package git.dimitrikvirik.contactbook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import git.dimitrikvirik.contactbook.mapper.ContactBookMapper;
import git.dimitrikvirik.contactbook.model.dto.ContactBookDTO;
import git.dimitrikvirik.contactbook.model.entity.ContactBookEntity;
import git.dimitrikvirik.contactbook.model.param.ContactBookParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureDataMongo
@SpringBootTest(
        properties = "de.flapdoodle.mongodb.embedded.version=5.0.5"
)
@EnableAutoConfiguration
@AutoConfigureMockMvc
@AutoConfigureDataJpa
class ContactBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoOperations mongoOperations;


    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Add contact book when unauthorized")
    void addContactBook_when_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/contact-book")
                        .content(objectMapper.writeValueAsString(ContactBookParam.builder().firstname("test").lastname("test").phone("test").email("test").address("test").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Add contact book when not has authority")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ"})
    void addContactBook_when_not_has_authority() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/contact-book")
                        .content(objectMapper.writeValueAsString(ContactBookParam.builder().firstname("test").lastname("test").phone("test").email("test").address("test").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Add contact book when success")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_WRITE"})
    void addContactBook_when_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/contact-book")
                        .content(objectMapper.writeValueAsString(ContactBookParam.builder().firstname("test").lastname("test").phone("test").email("test").address("test").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastname").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("test"));
    }


    @Test
    @DisplayName("Get contact book by id")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    void getContactBookById_when_success() throws Exception {
        ContactBookEntity contactBookEntity = getContactBookEntity("test");


        String id = contactBookEntity.getId();
        String contentAsStringFromId = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/contact-book/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(ContactBookMapper.toDTO(contactBookEntity), objectMapper.readValue(contentAsStringFromId, ContactBookDTO.class));
    }

    @Test
    @DisplayName("Get contact book by id when not found")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    void getContactBookById_when_not_found() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/contact-book/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get contact book by id when not mine")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    void getContactBookById_when_not_mine() throws Exception {
        ContactBookEntity contactBookEntity = getContactBookEntity("test2");

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/contact-book/" + contactBookEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete contact book by id when success")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    void deleteContactBookById_when_success() throws Exception {
        ContactBookEntity contactBookEntity = getContactBookEntity("test");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/contact-book/" + contactBookEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("Delete contact book by id when not found")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    void deleteContactBookById_when_not_found() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/contact-book/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Delete contact book by id when not mine")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    void deleteContactBookById_when_not_mine() throws Exception {
        ContactBookEntity contactBookEntity = getContactBookEntity("test2");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/contact-book/" + contactBookEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update contact book when success")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    void updateContactBook_when_success() throws Exception {
        ContactBookEntity contactBookEntity = getContactBookEntity("test");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/contact-book/" + contactBookEntity.getId())
                        .content(objectMapper.writeValueAsString(ContactBookParam.builder().firstname("test2").lastname("test2").phone("test2").email("test2").address("test2").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("test2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastname").value("test2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("test2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("test2"));
    }

    @Test
    @DisplayName("Update contact book when not found")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    void updateContactBook_when_not_found() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/contact-book/123")
                        .content(objectMapper.writeValueAsString(ContactBookParam.builder().firstname("test2").lastname("test2").phone("test2").email("test2").address("test2").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update contact book when not mine")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    void updateContactBook_when_not_mine() throws Exception {
        ContactBookEntity contactBookEntity = getContactBookEntity("test2");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/contact-book/" + contactBookEntity.getId())
                        .content(objectMapper.writeValueAsString(ContactBookParam.builder().firstname("test2").lastname("test2").phone("test2").email("test2").address("test2").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get all contact books without search param")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAllContactBooks_when_success() throws Exception {
        getContactBookEntity("test");
        getContactBookEntity("test2");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/contact-book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].firstname").value("test"));
    }



    @Test
    @DisplayName("Get all contact books with search param")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAllContactBooks_when_success_with_search_param() throws Exception {
        getContactBookEntity("test");
        getContactBookEntity("test");
        getContactBookEntity("test2");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/contact-book?firstname=test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].firstname").value("test"));
    }

    @Test
    @DisplayName("Get all contact books with search param wrong")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAllContactBooks_when_success_with_search_param_wrong() throws Exception {
        getContactBookEntity("test");
        getContactBookEntity("test");
        getContactBookEntity("test2");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/contact-book?firstname=test2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("Get all contact books with all search params")
    @WithMockUser(username = "test", authorities = {"CONTACT_BOOK_READ", "CONTACT_BOOK_WRITE"})
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAllContactBooks_when_success_with_all_search_params() throws Exception {
        getContactBookEntity("test");
        getContactBookEntity("test");
        getContactBookEntity("test2");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/contact-book?firstname=test&lastname=test&phone=test&address=test&email=test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].firstname").value("test"));
    }


    private ContactBookEntity getContactBookEntity(String user) {
        return mongoOperations.save(ContactBookEntity.builder()
                .ownerUserId(user)
                .firstname("test")
                .lastname("test")
                .phone("test")
                .email("test")
                .address("test")
                .build());
    }

}