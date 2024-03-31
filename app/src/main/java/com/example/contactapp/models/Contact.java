package com.example.contactapp.models;

import androidx.annotation.NonNull;

public class Contact {
    /*
     {
            "Cid": 1,
            "Name": "Nevin Hobden",
            "Email": "n@n.com",
            "Phone": "111-111-1111",
            "PhoneType": "CELL"
        }
     */

    private final int Cid;
    private String Name;
    private final String Email;
    private final String Phone;
    private final String PhoneType;

    public Contact(int cid, String name, String email, String phone, String phoneType) {
        this.Cid = cid;
        Name = name;
        Email = email;
        Phone = phone;
        PhoneType = phoneType;
    }

    public int getCid() {
        return Cid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }


    public String getPhone() {
        return Phone;
    }


    public String getPhoneType() {
        return PhoneType;
    }


    @NonNull
    @Override
    public String toString() {
        return "Contact{" +
                "Cid=" + Cid +
                ", Name='" + Name + '\'' +
                ", Email='" + Email + '\'' +
                ", Phone='" + Phone + '\'' +
                ", PhoneType='" + PhoneType + '\'' +
                '}';
    }
}
