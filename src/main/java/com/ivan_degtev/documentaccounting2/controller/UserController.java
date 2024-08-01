package com.ivan_degtev.documentaccounting2.controller;

import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForAdmin;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForUser;
import com.ivan_degtev.documentaccounting2.dto.user.UserDTO;
import com.ivan_degtev.documentaccounting2.service.impl.UserServiceImpl;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping(path = "api/users")
@AllArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final UserUtils userUtils;

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDTO>> index() {
        List<UserDTO> users = userService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }
    @GetMapping("/current-user")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) throws NullPointerException {
        UserDTO userDTO = userService.getCurrentUser(authentication);
        return ResponseEntity.ok(userDTO);
    }

    /**
    ручки для проверки во фронте является ли автор текущего документа/ файла  текущим авторизованным юзером
     */
    @GetMapping(path = "/check-current-user-is-author/{documentId}")
    public ResponseEntity<Boolean> checkCurrentUserIsAuthor(@PathVariable  Long documentId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userUtils.currentUserIsAuthorForDocuments(documentId));
    }
    @GetMapping(path = "/check-current-user-is-author-for-files/{fileId}")
    public ResponseEntity<Boolean> checkCurrentUserIsAuthorForFiles(@PathVariable  Long fileId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userUtils.currentUserIsAuthorForFiles(fileId));
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDTO> show(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.currentUser.idUser == #id or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> updateForUser(
            @RequestBody @Valid UpdateUserDTOForUser userData,
            @PathVariable Long id
    ) {
        UserDTO user = userService.updateForUser(userData, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
    @PutMapping(path = "/for-admin/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> updateForAdmin(
            @RequestBody @Valid UpdateUserDTOForAdmin userData,
            @PathVariable Long id
    ) {
        UserDTO user = userService.updateForAdmin(userData, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.currentUser.idUser == #id or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> updateUserWithNotFullField(
            @PathVariable Long id,
            @RequestBody UpdateUserDTOForUser updateUserDTO
    ) {
        UserDTO userDTO = userService.updateUserWithNotFullField(updateUserDTO, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDTO);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userUtils.currentUser.idUser == #id or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
