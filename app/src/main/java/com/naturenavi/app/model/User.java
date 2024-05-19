package com.naturenavi.app.model;

import java.util.List;

public class User {

    private String fullName;
    private String phoneNumber;
    private String email;

    private List<String> bookedTripIds;

    private String profileImageUrl;

    public User() {
    }


    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<String> getBookedTripIds() {
        return bookedTripIds;
    }

    public void setBookedTripIds(List<String> bookedTripIds) {
        this.bookedTripIds = bookedTripIds;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
