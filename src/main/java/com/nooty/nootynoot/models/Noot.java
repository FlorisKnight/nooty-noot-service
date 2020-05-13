package com.nooty.nootynoot.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Noot {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Id
    private String id;
    private String text;
    private String timestamp;
    private String userId;
}
