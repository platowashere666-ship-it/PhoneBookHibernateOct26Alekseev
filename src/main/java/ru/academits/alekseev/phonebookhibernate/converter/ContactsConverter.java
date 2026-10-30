package ru.academits.alekseev.phonebookhibernate.converter;

import org.springframework.stereotype.Service;
import ru.academits.alekseev.phonebookhibernate.entity.Contact;
import ru.academits.alekseev.phonebookhibernate.dto.ContactDto;

@Service
public class ContactsConverter implements Converter<Contact, ContactDto> {
    @Override
    public ContactDto convertToDto(Contact model) {
        return new ContactDto(model.getId(), model.getSurname(), model.getName(), model.getPhone());
    }

    @Override
    public Contact convertToModel(ContactDto dto) {
        return new Contact(dto.getId(), dto.getSurname(), dto.getName(), dto.getPhone());
    }
}
