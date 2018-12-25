package com.eddie.servercontacts;

public class Contact {
    private String name;
    private String lastName;
    private long id;
    private String phone;
    private String email;
    private String address;
    private String description;

    public Contact() {
    }

    public Contact(String name, String lastName, long id, String phone, String email, String address, String description) {
        this.name = name;
        this.lastName = lastName;
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public long getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", id=" + id +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){return true;}
        if(obj instanceof Contact){
            Contact tmp = (Contact) obj;
            return this.getId() == tmp.getId();
        }
        return false;
    }
}

