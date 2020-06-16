package com.nooty.nootynoot.viewmodels;

public class HashtagViewModel {
    private String userId;
    private String hashtag;
    private String nootId;

    public String getNootId() {
        return nootId;
    }

    public void setNootId(String nootId) {
        this.nootId = nootId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }
}
