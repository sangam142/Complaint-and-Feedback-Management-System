package com.pesu.cfs.config;

import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.AccountStatus;
import com.pesu.cfs.model.enums.Role;
import com.pesu.cfs.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create default admin if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .email("admin@pesu.edu")
                    .role(Role.ADMIN)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            userRepository.save(admin);
            System.out.println(">>> Default admin created: admin / admin123");
        }

        // Create sample support staff
        if (!userRepository.existsByUsername("staff1")) {
            User staff = User.builder()
                    .username("staff1")
                    .password(passwordEncoder.encode("staff123"))
                    .fullName("Support Staff 1")
                    .email("staff1@pesu.edu")
                    .role(Role.SUPPORT_STAFF)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            userRepository.save(staff);
            System.out.println(">>> Default staff created: staff1 / staff123");
        }

        if (!userRepository.existsByUsername("staff2")) {
            User staff2 = User.builder()
                    .username("staff2")
                    .password(passwordEncoder.encode("staff123"))
                    .fullName("Support Staff 2")
                    .email("staff2@pesu.edu")
                    .role(Role.SUPPORT_STAFF)
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();
            userRepository.save(staff2);
            System.out.println(">>> Default staff created: staff2 / staff123");
        }
    }
}
