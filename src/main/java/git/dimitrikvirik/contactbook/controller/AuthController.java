package git.dimitrikvirik.contactbook.controller;

import git.dimitrikvirik.contactbook.facade.UserFacade;
import git.dimitrikvirik.contactbook.model.dto.TokenDTO;
import git.dimitrikvirik.contactbook.model.dto.UserDTO;
import git.dimitrikvirik.contactbook.model.param.UserLoginParam;
import git.dimitrikvirik.contactbook.model.param.UserRegParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "The Authentication Controller for handling registration and login requests")
public class AuthController {

    private final UserFacade userFacade;

    @PostMapping("/register")
    @Operation(summary = "Create new user", description = "This operation creates a new user",
            tags = {"AuthController"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid UserRegParam supplied"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    public ResponseEntity<UserDTO> createUser(
            @Parameter(description = "User details for the new user to be created")
            @RequestBody UserRegParam userDTO) {
        return new ResponseEntity<>(userFacade.createUser(userDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in user", description = "This operation logs in a user",
            tags = {"AuthController"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged in successfully",
                    content = @Content(schema = @Schema(implementation = TokenDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid UserLoginParam supplied"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    public ResponseEntity<TokenDTO> login(
            @Parameter(description = "User login details")
            @RequestBody UserLoginParam userDTO) {
        return ResponseEntity.ok(userFacade.login(userDTO));
    }


}
