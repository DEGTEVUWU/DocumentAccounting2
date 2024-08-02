package com.ivan_degtev.documentaccounting2.controller.junit5Test.user;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.ivan_degtev.documentaccounting2.config.security.UserDetailsImpl;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForAdmin;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForUser;
import com.ivan_degtev.documentaccounting2.dto.user.UserDTO;
import com.ivan_degtev.documentaccounting2.mapper.UserMapper;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;
    private Authentication authentication;
    private UpdateUserDTOForUser updateUserDTOForUser;
    private UpdateUserDTOForAdmin updateUserDTOForAdmin;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setIdUser(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");

        authentication = mock(Authentication.class);

        updateUserDTOForUser = new UpdateUserDTOForUser();
        updateUserDTOForUser.setName(JsonNullable.of("newName"));
        updateUserDTOForUser.setEmail(JsonNullable.of("newEmail@example.com"));
        updateUserDTOForUser.setPassword(JsonNullable.of("newPassword"));
        updateUserDTOForUser.setUsername(JsonNullable.of("newUsername"));

        updateUserDTOForAdmin = new UpdateUserDTOForAdmin();
        updateUserDTOForAdmin.setRoleIds(JsonNullable.of(Set.of(1L)));
    }

    @Test
    void testGetAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        List<UserDTO> result = userService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDTO(user);
    }

    @Test
    void testGetCurrentUser() {
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getCurrentUser(authentication);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userMapper, times(1)).toDTO(user);
    }

    @Test
    void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toDTO(user);
    }

    @Test
    void testSave() {
        userService.save(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateForUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateForUser(updateUserDTOForUser, user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.updateForUser(updateUserDTOForUser, 1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).updateForUser(updateUserDTOForUser, user);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDTO(user);
    }
    @Test
    void testUpdateForAdmin() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateForAdmin(updateUserDTOForAdmin, user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.updateForAdmin(updateUserDTOForAdmin, 1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).updateForAdmin(updateUserDTOForAdmin, user);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDTO(user);
    }

    @Test
    void testDeleteById() {
        doNothing().when(userRepository).deleteById(1L);
        userService.delete(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteByUser() {
        doNothing().when(userRepository).delete(user);
        userService.delete(user);
        verify(userRepository, times(1)).delete(user);
    }
    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }
}
