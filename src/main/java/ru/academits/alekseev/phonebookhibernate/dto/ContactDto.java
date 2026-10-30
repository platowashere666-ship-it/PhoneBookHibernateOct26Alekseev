package ru.academits.alekseev.phonebookhibernate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContactDto {
    private Integer id;
    private String surname;
    private String name;
    private String phone;
}
