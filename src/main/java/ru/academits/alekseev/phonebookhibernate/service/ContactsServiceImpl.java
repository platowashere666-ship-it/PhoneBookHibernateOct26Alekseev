package ru.academits.alekseev.phonebookhibernate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.academits.alekseev.phonebookhibernate.converter.ContactsConverter;
import ru.academits.alekseev.phonebookhibernate.entity.Contact;
import ru.academits.alekseev.phonebookhibernate.repository.ContactsRepository;
import ru.academits.alekseev.phonebookhibernate.dto.BaseResponse;
import ru.academits.alekseev.phonebookhibernate.dto.ContactDto;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class ContactsServiceImpl implements ContactsService {
    private final ContactsRepository contactsRepository;
    private final ContactsConverter contactsConverter;
    private static final Random random = new Random();

    public ContactsServiceImpl(ContactsRepository contactsRepository, ContactsConverter contactsConverter) {
        this.contactsRepository = contactsRepository;
        this.contactsConverter = contactsConverter;
    }

    @Override
    public List<ContactDto> getContacts(String term) {
        log.debug("getContacts({})", term);

        if (term == null || term.isBlank()) {
            return contactsConverter.convertToDto(contactsRepository.findAll());
        }

        return contactsConverter.convertToDto(contactsRepository.findBySurnameContainingOrNameContainingOrPhoneContainingIgnoreCase(term, term, term));
    }

    @Override
    public BaseResponse createContact(ContactDto contact) {
        BaseResponse validationResult = validateContact(contact);

        if (!validationResult.isSuccess()) {
            log.error("createContact({})", validationResult);

            return validationResult;
        }

        contactsRepository.saveAndFlush(contactsConverter.convertToModel(contact));
        log.debug("createContact({})", contact);

        return BaseResponse.success();
    }

    @Override
    public BaseResponse editContact(ContactDto contact) {
        BaseResponse validationResult = validateContact(contact);

        if (!validationResult.isSuccess()) {
            log.error("editContact({})", validationResult);

            return validationResult;
        }

        if (!contactsRepository.existsById(contact.getId())) {
            log.error("editContact(Contact not found): {})", contact);

            return BaseResponse.error("Contact not found");
        }

        contactsRepository.saveAndFlush(contactsConverter.convertToModel(contact));
        log.debug("editContact({})", contact);

        return BaseResponse.success();
    }

    @Override
    public BaseResponse deleteContact(int id) {
        contactsRepository.deleteById(id);
        log.debug("deleteContact({})", id);

        return BaseResponse.success();
    }

    @Override
    public void  deleteRandomContact() {
        List<Contact> contacts = contactsRepository.findAll();

        if (contacts.isEmpty()) {
            log.info("No contacts found");
            return;
        }

        Contact randomContact = contacts.get(random.nextInt(contacts.size()));
        contactsRepository.deleteById(randomContact.getId());

        log.info("deleteContact({})", randomContact);
    }

    private BaseResponse validateContact(ContactDto contact) {
        if (contact == null) {
            return BaseResponse.error("Contact is null");
        }

        if (contact.getSurname() == null || contact.getSurname().isBlank()) {
            return BaseResponse.error("Surname is empty or null");
        }

        if (contact.getName() == null || contact.getName().isBlank()) {
            return BaseResponse.error("Name is empty or null");
        }

        if (contact.getPhone() == null || contact.getPhone().isBlank()) {
            return BaseResponse.error("Phone is empty or null");
        }

        BaseResponse phoneValidationResult = validatePhone(contact.getPhone());

        if (!phoneValidationResult.isSuccess()) {
            return phoneValidationResult;
        }

        return BaseResponse.success();
    }

    private BaseResponse validatePhone(String phone) {
        List<Contact> contacts = contactsRepository.findAll();

        boolean phoneExists = contacts.stream()
                .anyMatch(c -> c.getPhone().equals(phone));

        if (phoneExists) {
            return BaseResponse.error("Phone already exists");
        }

        return BaseResponse.success();
    }
}
