package ru.academits.alekseev.phonebookhibernate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.academits.alekseev.phonebookhibernate.entity.Contact;

import java.util.List;

public interface ContactsRepository extends JpaRepository<Contact,Integer> {
    List<Contact> findBySurnameContainingOrNameContainingOrPhoneContainingIgnoreCase(String surname, String name, String phone);
}
