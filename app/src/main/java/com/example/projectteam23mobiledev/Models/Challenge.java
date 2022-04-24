package com.example.projectteam23mobiledev.Models;

import com.example.projectteam23mobiledev.Utilities.Enums.StatusEnum;

import java.io.Serializable;

public class Challenge implements Serializable {
    String type;
    Double distance;
    Double time;
    String receiver;
    String sender;
    Integer minPoints;
    Long timeStamp;
    Integer totalCredit;
    String status;
    StatusEnum sendStatus;
    StatusEnum receiverStatus;

    public StatusEnum getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(StatusEnum sendStatus) {
        this.sendStatus = sendStatus;
    }

    public StatusEnum getReceiverStatus() {
        return receiverStatus;
    }

    public void setReceiverStatus(StatusEnum receiverStatus) {
        this.receiverStatus = receiverStatus;
    }

    public Challenge(String type, Double distance, Double time, String receiver, String sender, Integer minPoints, Long timeStamp, Integer totalCredit, String status, StatusEnum sendStatus, StatusEnum receiverStatus) {
        this.type = type;
        this.distance = distance;
        this.time = time;
        this.receiver = receiver;
        this.sender = sender;
        this.minPoints = minPoints;
        this.timeStamp = timeStamp;
        this.totalCredit = totalCredit;
        this.status = status;
        this.sendStatus = sendStatus;
        this.receiverStatus = receiverStatus;
    }

    public Challenge(){};

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Integer getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(Integer minPoints) {
        this.minPoints = minPoints;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(Integer totalCredit) {
        this.totalCredit = totalCredit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
