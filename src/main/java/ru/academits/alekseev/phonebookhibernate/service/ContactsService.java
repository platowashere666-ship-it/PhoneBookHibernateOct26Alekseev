package ru.academits.alekseev.phonebookhibernate.service;

import ru.academits.alekseev.phonebookhibernate.dto.BaseResponse;
import ru.academits.alekseev.phonebookhibernate.dto.ContactDto;

import java.util.List;

public interface ContactsService {
    List<ContactDto> getContacts(String term);

    BaseResponse createContact(ContactDto contact);

    BaseResponse editContact(ContactDto contact);

    BaseResponse deleteContact(int id);
}
