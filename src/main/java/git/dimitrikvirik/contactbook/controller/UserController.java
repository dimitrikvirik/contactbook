package git.dimitrikvirik.contactbook.controller;

import git.dimitrikvirik.contactbook.facade.UserFacade;
import git.dimitrikvirik.contactbook.model.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "The User Controller for handling user requests")
public class UserController {

    private final UserFacade userFacade;

    @GetMapping
    @Operation(summary = "Get current user", description = "This operation retrieves the current user",
            tags = {"UserController"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved user successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have necessary permissions"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    public ResponseEntity<UserDTO> getCurrentUser(@Parameter(hidden = true) Principal principal) {
        return ResponseEntity.ok(userFacade.getUser(principal.getName()));
    }
}
