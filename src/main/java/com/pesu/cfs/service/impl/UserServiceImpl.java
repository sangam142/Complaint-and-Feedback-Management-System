package com.pesu.cfs.service.impl;

import com.pesu.cfs.dto.RegistrationDto;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.AccountStatus;
import com.pesu.cfs.model.enums.Role;
import com.pesu.cfs.pattern.factory.NotificationFactory;
import com.pesu.cfs.repository.NotificationRepository;
import com.pesu.cfs.repository.UserRepository;
import com.pesu.cfs.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationRepository notificationRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public User registerCustomer(RegistrationDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .role(Role.CUSTOMER)
                .accountStatus(AccountStatus.ACTIVE) // Auto-verify for simplicity
                .build();
        return userRepository.save(user);
    }

    @Override
    public User createStaff(RegistrationDto dto, Role role) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .role(role)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> findActiveStaff() {
        return userRepository.findByRoleAndAccountStatus(Role.SUPPORT_STAFF, AccountStatus.ACTIVE);
    }

    @Override
    public void updateAccountStatus(Long userId, AccountStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        switch (status) {
            case ACTIVE -> user.reinstateAccount();
            case SUSPENDED -> user.suspendAccount();
            case DEACTIVATED -> user.deactivateAccount();
            default -> {}
        }
        userRepository.save(user);

        // Factory pattern: create notification for account status change
        var notification = NotificationFactory.createAccountAction(user, status.name());
        notification.send();
        notification.deliver();
        notificationRepository.save(notification);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
