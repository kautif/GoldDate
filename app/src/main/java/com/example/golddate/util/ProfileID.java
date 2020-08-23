package com.example.golddate.util;

public class ProfileID {
    String profileID;
    public <userID extends ProfileID> userID withId(String providedID) {
        this.profileID = providedID;
        return (userID) this;
    }
}
