package com.sheygam.masa_2018_g1_retrofit;

public class Contact {
    String name;
    String lastName;
    String phone;
    String email;
    String address;
    long id;
    String description;

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
