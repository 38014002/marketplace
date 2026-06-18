package com.marketplace.ms_user.config;

import com.marketplace.ms_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleMigrationRunner implements ApplicationRunner {

    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        userRepository.findByUsername("juan").ifPresent(user -> {
            String role = user.getRole();
            if (role == null || (!"ADMIN".equals(role) && !"ROLE_ADMIN".equals(role))) {
                user.setRole("ADMIN");
                userRepository.save(user);
            }
        });
    }
}
