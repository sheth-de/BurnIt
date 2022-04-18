package com.example.projectteam23mobiledev.Models;

public class ChallengeCardModel {

    String title, details, created_by;
    Long date;
    String challengeId;
    Challenge challenge;

    public ChallengeCardModel(String title, String details, Long date, String created_by, String challengeId, Challenge challenge) {
        this.title = title;
        this.details = details;
        this.date = date;
        this.created_by = created_by;
        this.challengeId = challengeId;
        this.challenge = challenge;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
}
