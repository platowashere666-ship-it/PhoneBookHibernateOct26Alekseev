package ru.academits.alekseev.phonebookhibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.academits.alekseev.phonebookhibernate.entity.Contact;
import ru.academits.alekseev.phonebookhibernate.security.entity.ApplicationUser;

import java.util.List;
import java.util.Optional;

public interface ContactsRepository extends JpaRepository<Contact, Integer> {
    List<Contact> findBySurnameContainingOrNameContainingOrPhoneContainingIgnoreCase(String surname, String name, String phone);

    List<Contact> findByUser(ApplicationUser user);

    List<Contact> findByUserAndSurnameContainingOrUserAndNameContainingOrUserAndPhoneContainingIgnoreCase(
            ApplicationUser user1, String surname,
            ApplicationUser user2, String name,
            ApplicationUser user3, String phone
    );

    boolean existsByIdAndUser(Integer id, ApplicationUser user);

    Optional<Contact> findByIdAndUser(Integer id, ApplicationUser user);

    boolean existsByPhoneAndUser(String phone, ApplicationUser user);
}
