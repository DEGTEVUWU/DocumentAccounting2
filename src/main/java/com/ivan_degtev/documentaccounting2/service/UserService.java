package com.ivan_degtev.documentaccounting2.service;

import com.ivan_degtev.documentaccounting2.dto.user.UserDTO;
import com.ivan_degtev.documentaccounting2.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    List<UserDTO> getAll();
    UserDTO findById(Long idUser);
    User findByUsername(String username);
    void save(User user);
    void delete(User user);
}
