package com.timov.studentenhuis.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;

public class Meal implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("idMaaltijd")
    private long id;

    @SerializedName("idKok")
    private long cookId;

    @SerializedName("naamStudent")
    private String cookName;

    @SerializedName("naamMaaltijd")
    private String name;

    @SerializedName("maaltijdAfbeelding")
    private String image;

    @SerializedName("beschrijving")
    private String description;

    @SerializedName("maxEters")
    private int maxParticipants;

    @SerializedName("kosten")
    private String price;

    @SerializedName("maaltijdBeginTijd")
    private Timestamp startTimestamp;


    public Meal() {
    }

    public Meal(long id, long cookId, String cookName, String name, String image, String description, int maxParticipants, String price, Timestamp startTimestamp) {
        this.id = id;
        this.cookId = cookId;
        this.cookName = cookName;
        this.name = name;
        this.image = image;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.startTimestamp = startTimestamp;
    }

    public Meal(long cookId, String cookName, String name, String image, String description, int maxParticipants, String price, Timestamp startTimestamp) {
        this.cookId = cookId;
        this.cookName = cookName;
        this.name = name;
        this.image = image;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.startTimestamp = startTimestamp;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Timestamp startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setCookId(long cookId) {
        this.cookId = cookId;
    }

    public String getCookName() {
        return cookName;
    }
}
