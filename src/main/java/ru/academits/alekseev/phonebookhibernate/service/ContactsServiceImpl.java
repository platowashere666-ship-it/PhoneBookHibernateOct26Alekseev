package ru.academits.alekseev.phonebookhibernate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.academits.alekseev.phonebookhibernate.converter.ContactsConverter;
import ru.academits.alekseev.phonebookhibernate.entity.Contact;
import ru.academits.alekseev.phonebookhibernate.repository.ContactsRepository;
import ru.academits.alekseev.phonebookhibernate.dto.BaseResponse;
import ru.academits.alekseev.phonebookhibernate.dto.ContactDto;
import ru.academits.alekseev.phonebookhibernate.security.entity.ApplicationUser;
import ru.academits.alekseev.phonebookhibernate.security.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
public class ContactsServiceImpl implements ContactsService {
    private final ContactsRepository contactsRepository;
    private final ContactsConverter contactsConverter;
    private final UserRepository userRepository;
    private static final Random random = new Random();

    public ContactsServiceImpl(ContactsRepository contactsRepository, ContactsConverter contactsConverter, UserRepository userRepository) {
        this.contactsRepository = contactsRepository;
        this.contactsConverter = contactsConverter;
        this.userRepository = userRepository;
    }

    private ApplicationUser getCurrentUser() {
        Object principal = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        String username;

        if ((principal instanceof UserDetails)) {
            username = ((UserDetails) principal).getUsername();
        } else {
            assert principal != null;
            username = principal.toString();
        }

        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    @Override
    public List<ContactDto> getContacts(String term) {
        ApplicationUser currentUser = getCurrentUser();
        log.debug("getContacts({}) for user {}", term, currentUser.getUsername());

        if (term == null || term.isBlank()) {
            return contactsConverter.convertToDto(contactsRepository.findByUser(currentUser));
        }

        return contactsConverter.convertToDto(
                contactsRepository.findByUserAndSurnameContainingOrUserAndNameContainingOrUserAndPhoneContainingIgnoreCase(
                        currentUser, term, currentUser, term, currentUser, term
                )
        );
    }

    @Override
    public BaseResponse createContact(ContactDto contactDto) {
        BaseResponse validationResult = validateContact(contactDto, null);

        if (!validationResult.isSuccess()) {
            log.error("createContact({})", validationResult);

            return validationResult;
        }

        ApplicationUser currentUser = getCurrentUser();
        Contact contact = contactsConverter.convertToModel(contactDto);
        contact.setUser(currentUser);

        contactsRepository.saveAndFlush(contact);
        log.debug("createContact({}) by {}", contactDto, currentUser.getUsername());

        return BaseResponse.success();
    }

    @Override
    public BaseResponse editContact(ContactDto contactDto) {
        BaseResponse validationResult = validateContact(contactDto, contactDto.getId());

        if (!validationResult.isSuccess()) {
            return validationResult;
        }

        ApplicationUser currentUser = getCurrentUser();
        Contact existingContact = contactsRepository.findByIdAndUser(contactDto.getId(), currentUser).orElse(null);

        if (existingContact == null) {
            return BaseResponse.error("Contact not found or access denied");
        }

        Contact contact = contactsConverter.convertToModel(contactDto);
        contact.setUser(currentUser);

        contactsRepository.saveAndFlush(contact);
        log.debug("editContact({}) by {}", contactDto, currentUser.getUsername());

        return BaseResponse.success();
    }

    @Override
    public BaseResponse deleteContact(int id) {
        ApplicationUser currentUser = getCurrentUser();

        if (!contactsRepository.existsByIdAndUser(id, currentUser)) {
            return BaseResponse.error("Contact not found or access denied");
        }

        contactsRepository.deleteById(id);
        log.debug("deleteContact({}) by {}", id, currentUser.getUsername());

        return BaseResponse.success();
    }

    @Override
    public void deleteRandomContact() {
        List<Contact> contacts = contactsRepository.findAll();

        if (contacts.isEmpty()) {
            log.info("No contacts found");
            return;
        }

        Contact randomContact = contacts.get(random.nextInt(contacts.size()));
        contactsRepository.deleteById(randomContact.getId());

        log.info("deleteContact({})", randomContact);
    }

    private BaseResponse validateContact(ContactDto contact, Integer excludeId) {
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

        return validatePhone(contact.getPhone(), excludeId);
    }

    private BaseResponse validatePhone(String phone, Integer excludeId) {
        ApplicationUser currentUser = getCurrentUser();

        boolean phoneExists = contactsRepository.existsByPhoneAndUser(phone, currentUser);

        if (phoneExists && excludeId != null) {
            Contact existingContact = contactsRepository.findByIdAndUser(excludeId, currentUser).orElse(null);

            if (existingContact != null && existingContact.getPhone().equals(phone)) {
                return BaseResponse.success();
            }
        }

        if (phoneExists) {
            return BaseResponse.error("Phone is already exists");
        }

        return BaseResponse.success();
    }
}
