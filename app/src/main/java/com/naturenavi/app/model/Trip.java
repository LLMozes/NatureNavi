package com.naturenavi.app.model;

import java.util.Date;

public class Trip {
    private String id;
    private String price;
    private String startDate;
    private String endDate;
    private String description;
    private String name;
    private int imageResource;
    private int participant;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Trip() {
    }

    public Trip(String price, String startDate, String endDate, String description, String name, int imageResource, int participant) {
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.name = name;
        this.imageResource = imageResource;
        this.participant = participant;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getParticipant() {
        return participant;
    }

    public void setParticipant(int participant) {
        this.participant = participant;
    }
}
