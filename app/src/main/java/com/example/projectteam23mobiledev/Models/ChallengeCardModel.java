package com.example.projectteam23mobiledev.Models;

public class ChallengeCardModel {

    String title, details, date, created_by;

    public ChallengeCardModel(String title, String details, String date, String created_by) {
        this.title = title;
        this.details = details;
        this.date = date;
        this.created_by = created_by;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
}
