package com.example.contactapp.models;

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

    private int Cid;
    private String Name;
    private String Email;
    private String Phone;
    private String PhoneType;

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

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPhoneType() {
        return PhoneType;
    }

    public void setPhoneType(String phoneType) {
        PhoneType = phoneType;
    }

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
