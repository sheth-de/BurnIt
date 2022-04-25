package com.example.projectteam23mobiledev.Models;

import java.io.Serializable;

public class RunModel implements Serializable {
    String id;
    String user;
    Double distance;
    int steps;
    double speed;
    long seconds;
    String challengeId;
    double calories;

    public RunModel() {
    }

    public RunModel(String user, Double distance,
                    int steps, double speed, long seconds,
                    String challengeId, double calories) {
        this.user = user;
        this.distance = distance;
        this.steps = steps;
        this.speed = speed;
        this.seconds = seconds;
        this.challengeId = challengeId;
        this.calories = calories;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public double getCalories() {
        return calories;
    }

    public String getUser() {
        return user;
    }

    public Double getDistance() {
        return distance;
    }

    public int getSteps() {
        return steps;
    }

    public double getSpeed() {
        return speed;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }
}
