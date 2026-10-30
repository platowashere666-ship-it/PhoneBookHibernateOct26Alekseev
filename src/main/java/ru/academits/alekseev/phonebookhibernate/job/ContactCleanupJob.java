package ru.academits.alekseev.phonebookhibernate.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.academits.alekseev.phonebookhibernate.entity.Contact;
import ru.academits.alekseev.phonebookhibernate.repository.ContactsRepository;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class ContactCleanupJob {
    private final ContactsRepository contactsRepository;
    private static final Random random = new Random();

    public ContactCleanupJob(ContactsRepository contactsRepository) {
        this.contactsRepository = contactsRepository;
    }

    public void cleanup(List<Contact> contacts) {
        Contact contact = contacts.get(random.nextInt(contacts.size()));

        contactsRepository.deleteById(contact.getId());
        log.info("Deleted contact {}", contact.getId());
    }
}
