package com.example.projectteam23mobiledev.Models;

public class RunModel {
    String user;
    Double distance;
    int steps;
    double speed;
    long seconds;

    public RunModel(String user, Double distance, int steps, double speed, long seconds) {
        this.user = user;
        this.distance = distance;
        this.steps = steps;
        this.speed = speed;
        this.seconds = seconds;
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
}
