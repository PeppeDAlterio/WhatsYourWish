package com.peppedalterio.whatsyourwish.pojo;

public class Contact {

    private String name;
    private String phoneNumber;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        setPhoneNumber(phoneNumber);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {

        phoneNumber = phoneNumber.replace("(","");
        phoneNumber = phoneNumber.replace(")","");
        phoneNumber = phoneNumber.replace("-","");
        phoneNumber = phoneNumber.replace("+","");
        phoneNumber = phoneNumber.replace(" ","");

        this.phoneNumber = phoneNumber;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
