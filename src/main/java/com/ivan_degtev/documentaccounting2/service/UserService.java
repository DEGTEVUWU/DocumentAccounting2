package com.ivan_degtev.documentaccounting2.service;

import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForAdmin;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForUser;
import com.ivan_degtev.documentaccounting2.dto.user.UserDTO;
import com.ivan_degtev.documentaccounting2.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserDTO getCurrentUser(Authentication authentication);
    List<UserDTO> getAll();
    UserDTO findById(Long idUser);
    User findByUsername(String username);
    void save(User user);
    UserDTO updateForUser(UpdateUserDTOForUser userData, Long id);
    UserDTO updateForAdmin(UpdateUserDTOForAdmin userData, Long id);
    UserDTO updateUserWithNotFullField(UpdateUserDTOForUser userData, Long id);
    void delete(Long idUser);
    void delete(User user);
}
