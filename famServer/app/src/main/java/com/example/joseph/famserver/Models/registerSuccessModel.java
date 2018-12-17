package com.example.joseph.famserver.Models;

public class registerSuccessModel extends JsonModel {
    public registerSuccessModel(String authToken, String userName, String personID) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
    }

    public String getAuthToken() { return authToken; }
    public String getUserName() { return userName; }
    public String getPersonID() { return personID; }
    public String getMessage() { return message; }

    String authToken;
    String userName;
    String personID;
    String message;

    public registerSuccessModel(String message) {
        this.message = message;
    }
}
