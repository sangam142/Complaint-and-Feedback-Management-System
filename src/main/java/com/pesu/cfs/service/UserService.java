package com.pesu.cfs.service;

import com.pesu.cfs.dto.RegistrationDto;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.AccountStatus;
import com.pesu.cfs.model.enums.Role;

import java.util.List;
import java.util.Optional;

/**
 * Design Principle: DIP — Controllers depend on this interface,
 * not on the concrete UserServiceImpl.
 */
public interface UserService {
    User registerCustomer(RegistrationDto dto);
    User createStaff(RegistrationDto dto, Role role);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    List<User> findByRole(Role role);
    List<User> findActiveStaff();
    void updateAccountStatus(Long userId, AccountStatus status);
    List<User> findAll();
}
