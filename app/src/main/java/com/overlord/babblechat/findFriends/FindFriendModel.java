package com.overlord.babblechat.findFriends;

public class FindFriendModel {
    private String userName;
    private String photoName;
    private String userId;
    private Boolean requestSent;

    public FindFriendModel(String userName, String photoName, String userId, Boolean requestSent) {
        this.userName = userName;
        this.photoName = photoName;
        this.userId = userId;
        this.requestSent = requestSent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getRequestSent() {
        return requestSent;
    }

    public void setRequestSent(Boolean requestSent) {
        this.requestSent = requestSent;
    }
}
