package git.dimitrikvirik.contactbook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import git.dimitrikvirik.contactbook.model.param.UserRegParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureDataMongo
@SpringBootTest(
        properties = "de.flapdoodle.mongodb.embedded.version=5.0.5"
)
@EnableAutoConfiguration
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Register when successful")
    @DirtiesContext
    void register_when_successful() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .content(objectMapper.writeValueAsBytes(UserRegParam.builder().username("test").password("testtest").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.username").value("test")
                )
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.password").doesNotExist()
                );
    }

    @Test
    @DisplayName("Register when username is empty")
    void register_when_username_is_empty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .content(objectMapper.writeValueAsBytes(UserRegParam.builder().username("                ").password("testtest").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message").value("username must not be blank")
                );
    }

    @Test
    @DisplayName("Register when password is empty")
    void register_when_password_is_empty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .content(objectMapper.writeValueAsBytes(UserRegParam.builder().username("test").password("                ").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message").value("password must not be blank")
                );
    }


    @Test
    @DisplayName("Register when username length is less than 3")
    void register_when_username_length_is_less_than_3() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .content(objectMapper.writeValueAsBytes(UserRegParam.builder().username("te").password("testtest").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message").value("username length must be between 3 and 2147483647")
                );
    }

    @Test
    @DisplayName("Register when password length is less than 6")
    void register_when_password_length_is_less_than_6() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .content(objectMapper.writeValueAsBytes(UserRegParam.builder().username("test").password("testt").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message").value("password length must be between 6 and 2147483647")
                );
    }


    @Test
    @DisplayName("Register when username exist")
    @DirtiesContext
    void register_when_username_exist() throws Exception {
        register_when_successful();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/register")
                        .content(objectMapper.writeValueAsBytes(UserRegParam.builder().username("test").password("testtest").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message").value("User with username test already exist")
                );
    }


    @Test
    @DisplayName("Login when successful")
    @DirtiesContext
    void login_when_successful() throws Exception {
        String token = loginAndGetToken();

        Base64.Decoder decoder = Base64.getUrlDecoder();

        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        String username = objectMapper.readTree(payload).get("username").asText();
        //Validated token
        assertEquals("test", username);
    }


    @Test
    @DisplayName("Login when wrong username")
    @DirtiesContext
    void login_when_wrong_username() throws Exception {
        register_when_successful();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content(objectMapper.writeValueAsBytes(UserRegParam.builder().username("test1").password("testtest").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message").value("Wrong credentials")
                );
    }

    @Test
    @DisplayName("Login when wrong password")
    @DirtiesContext
    void login_when_wrong_password() throws Exception {
        register_when_successful();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content(objectMapper.writeValueAsBytes(UserRegParam.builder().username("test").password("testtest1").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.message").value("Wrong credentials")
                );
    }

    @Test
    @DisplayName("Get current user when successful")
    @DirtiesContext
    void getCurrentUser() throws Exception {
        String token = loginAndGetToken();


        Base64.Decoder decoder = Base64.getUrlDecoder();

        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.username").value("test")
                )
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.id").value(objectMapper.readTree(payload).get("sub").asText())
                )
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.password").doesNotExist()
                );
    }



    private String loginAndGetToken() throws Exception {
        register_when_successful();

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content(objectMapper.writeValueAsBytes(UserRegParam.builder().username("test").password("testtest").build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.token").exists()
                )

                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(contentAsString).get("token").asText();
    }
}