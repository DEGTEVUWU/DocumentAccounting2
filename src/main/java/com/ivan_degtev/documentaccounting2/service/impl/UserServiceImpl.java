package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.config.security.UserDetailsImpl;
import com.ivan_degtev.documentaccounting2.dto.address.AddressUpdateDTO;
import com.ivan_degtev.documentaccounting2.dto.user.BaseUpdateUserDTO;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForAdmin;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForUser;
import com.ivan_degtev.documentaccounting2.dto.user.UserDTO;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.mapper.UserMapper;
import com.ivan_degtev.documentaccounting2.model.AddressEntity;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AddressServiceImpl addressService;

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAll() {
        var users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = findByUsername(userDetails.getUsername());
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found!"));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    /**
     * В двух следующих методах изменения данных юзера добавлена доп. логика по изменению
     * (по сущности первичному добавлению адреса юзера.
     * Идет перенаправления в AddressService и данные получаются оттуда(от внешнего API)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserDTO updateForUser(UpdateUserDTOForUser userData, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found!"));
        if(userData.getEnteredAddress().isPresent()) {
            user.setAddress(changeUserAddressIfAvailable(userData));
        }
        userMapper.updateForUser(userData, user);
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateForAdmin(UpdateUserDTOForAdmin userData, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found!"));
        if (userData.getEnteredAddress().isPresent()) {
            user.setAddress(changeUserAddressIfAvailable(userData));
        }
        userMapper.updateForAdmin(userData, user);
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    @Override
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

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }

    /**
     * Утилитный метод для части логики в сервисах изменения юзера - изменения адресса. Доступе для обоих сценариев
     * - работы из под Админа или Самим юзером
     */
    private AddressEntity changeUserAddressIfAvailable(BaseUpdateUserDTO updateUserDTO) {
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.setEnteredFullAddressForUpdate(updateUserDTO.getEnteredAddress().get());
        return addressService.updateAddress(addressUpdateDTO);

    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
    }

    /**
     * Метод используется в секьюрити для создания UserDetailsImpl, для последующего внедрения данных юзера в аутентификацию,
     * которая далее будет внедрена в контекст секьюрити. Все это будет вызывается из основного фильтра doFilterInternal.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
        return UserDetailsImpl.build(user);
    }
}
