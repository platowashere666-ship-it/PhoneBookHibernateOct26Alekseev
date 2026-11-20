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
        Contact contact = new Contact();

        contact.setId(dto.getId());
        contact.setSurname(dto.getSurname());
        contact.setName(dto.getName());
        contact.setPhone(dto.getPhone());

        return contact;
    }
}
