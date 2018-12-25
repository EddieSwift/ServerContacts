package com.eddie.servercontacts;

import java.util.ArrayList;
import java.util.List;

public class ContactsDto {
    private List<Contact> contacts;

    public ContactsDto() {
        contacts = new ArrayList<>();
    }

    public ContactsDto(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "ContactsDto{" +
                "contacts=" + contacts.toString() +
                '}';
    }
}
