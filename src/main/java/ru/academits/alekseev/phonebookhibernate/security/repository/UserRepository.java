package ru.academits.alekseev.phonebookhibernate.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.academits.alekseev.phonebookhibernate.security.entity.ApplicationUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByUsername(String username);
}
