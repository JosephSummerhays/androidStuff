package com.example.joseph.famserver.Models;

public class UserModel extends JsonModel {
    String userName;
    String password;
    String email;
    String firstName;
    String lastName;
    String gender;
    String personID;
    public UserModel(String userName, String password, String email,
                     String firstName, String lastName, String gender,
                     String personID) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return userName.equals(userModel.userName) &&
                password.equals(userModel.password) &&
                email.equals(userModel.email) &&
                firstName.equals(userModel.firstName) &&
                lastName.equals(userModel.lastName) &&
                gender.equals(userModel.gender);
    }

    public String getPassword() {
        return password;
    }
    public String getUserName() {
        return userName;
    }
    public String getEmail() { return email; }
    public String getFirst() { return firstName; }
    public String getLast() { return lastName; }
    public String getGender() { return gender; }
    public String getPerson() { return personID; }
}
