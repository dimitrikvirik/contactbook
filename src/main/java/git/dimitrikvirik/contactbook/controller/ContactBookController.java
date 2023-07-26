package git.dimitrikvirik.contactbook.controller;

import git.dimitrikvirik.contactbook.facade.ContactBookFacade;
import git.dimitrikvirik.contactbook.model.dto.ContactBookDTO;
import git.dimitrikvirik.contactbook.model.param.ContactBookParam;
import git.dimitrikvirik.contactbook.model.param.ContactBookSearchParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/contact-book")
@RequiredArgsConstructor
@Tag(name = "ContactBookController", description = "The Contact Book Controller for handling contact book requests")
public class ContactBookController {

    private final ContactBookFacade contactBookFacade;

    @PostMapping
    @PreAuthorize("hasAuthority('CONTACT_BOOK_WRITE')")
    @Operation(summary = "Create new contact book", description = "This operation creates a new contact book",
            tags = {"ContactBookController"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contact book created successfully",
                    content = @Content(schema = @Schema(implementation = ContactBookDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ContactBookParam supplied"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    public ResponseEntity<ContactBookDTO> addContactBook(@RequestBody ContactBookParam contactBookDTO, Principal principal) {
        return new ResponseEntity<>(contactBookFacade.addContactBook(contactBookDTO, principal), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTACT_BOOK_READ')")
    @Operation(summary = "Get contact book", description = "This operation retrieves a contact book",
            tags = {"ContactBookController"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved contact book successfully",
                    content = @Content(schema = @Schema(implementation = ContactBookDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have necessary permissions"),
            @ApiResponse(responseCode = "404", description = "Contact book not found"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    public ResponseEntity<ContactBookDTO> getContactBook(@PathVariable("id") String id) {
        return new ResponseEntity<>(contactBookFacade.getContactBook(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTACT_BOOK_WRITE')")
    @Operation(summary = "Delete contact book", description = "This operation deletes a contact book",
            tags = {"ContactBookController"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contact book deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have necessary permissions"),
            @ApiResponse(responseCode = "404", description = "Contact book not found"),
            @ApiResponse(responseCode = "500", description = "Server Error")})

    public ResponseEntity<Void> deleteContactBook(@PathVariable("id") String id, @Parameter(hidden = true) Principal principal) {
        contactBookFacade.deleteContactBook(id, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTACT_BOOK_WRITE')")
    @Operation(summary = "Update contact book", description = "This operation updates a contact book",
            tags = {"ContactBookController"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact book updated successfully",
                    content = @Content(schema = @Schema(implementation = ContactBookDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ContactBookParam supplied"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have necessary permissions"),
            @ApiResponse(responseCode = "404", description = "Contact book not found"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    public ResponseEntity<ContactBookDTO> updateContactBook(@PathVariable("id") String id, @RequestBody ContactBookParam contactBookDTO, @Parameter(hidden = true) Principal principal) {
        return new ResponseEntity<>(contactBookFacade.updateContactBook(id, contactBookDTO, principal), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CONTACT_BOOK_READ')")
    @Operation(summary = "Get all contact books", description = "This operation retrieves all contact books",
            tags = {"ContactBookController"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved contact books successfully",
                    content = @Content(schema = @Schema(implementation = ContactBookDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have necessary permissions"),
            @ApiResponse(responseCode = "500", description = "Server Error")})
    @PageableAsQueryParam
    public ResponseEntity<Page<ContactBookDTO>> getAllContactBooks(
            ContactBookSearchParam searchParam,
            @Parameter(hidden = true)
            @PageableDefault
            Pageable pageable,
            @Parameter(hidden = true)
            Principal principal) {
        return new ResponseEntity<>(contactBookFacade.getAllContactBooks(searchParam, principal, pageable), HttpStatus.OK);
    }
}
