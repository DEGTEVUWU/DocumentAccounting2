package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.config.security.UserDetailsImpl;
import com.ivan_degtev.documentaccounting2.controller.UserController;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForAdmin;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForUser;
import com.ivan_degtev.documentaccounting2.dto.user.UserDTO;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.mapper.UserMapper;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public List<UserDTO> getAll() {
        var users = userRepository.findAll();
        List<UserDTO> result = users.stream()
                .map(userMapper::toDTO)
                .toList();
        return result;
    }


    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found!"));
        var userDTO = userMapper.toDTO(user);
        return userDTO;
    }

    public UserDTO updateForUser(UpdateUserDTOForUser userData, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found!"));
        userMapper.updateForUser(userData, user);
        userRepository.save(user);
        var userDTO = userMapper.toDTO(user);
        return userDTO;
    }
    public UserDTO updateForAdmin(UpdateUserDTOForAdmin userData, Long id) {
        logger.info("зашёл в метод изменения юзера для админа");
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found!"));
        logger.info("нашёл юзера из БД");
        userMapper.updateForAdmin(userData, user);
        logger.info("замапил апдет юзер");
        userRepository.save(user);
        logger.info("сохранил в БД обновленного юзера {}", user);
        var userDTO = userMapper.toDTO(user);
        return userDTO;
    }
    @Transactional
    public UserDTO updateUserWithNotFullField(UpdateUserDTOForUser userData, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        if (userData.getName() != null) {
            userData.getName().ifPresent(user::setName);
        }
        if (userData.getEmail() != null) {
            userData.getEmail().ifPresent(user::setEmail);
        }
        if (userData.getPassword() != null) {
            userData.getPassword().ifPresent(user::setPassword);
        }
        if (userData.getUsername() != null) {
            userData.getUsername().ifPresent(user::setUsername);
        }
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
        return UserDetailsImpl.build(user);
    }
}
