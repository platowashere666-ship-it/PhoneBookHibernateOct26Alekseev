package ru.academits.alekseev.phonebookhibernate.security.service;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.academits.alekseev.phonebookhibernate.security.entity.ApplicationUser;
import ru.academits.alekseev.phonebookhibernate.security.entity.Role;
import ru.academits.alekseev.phonebookhibernate.security.repository.RoleRepository;
import ru.academits.alekseev.phonebookhibernate.security.repository.UserRepository;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username is already in use");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });

        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUsername(username);
        applicationUser.setPassword(passwordEncoder.encode(password));
        applicationUser.getRoles().add(userRole);

        userRepository.save(applicationUser);
    }
}
