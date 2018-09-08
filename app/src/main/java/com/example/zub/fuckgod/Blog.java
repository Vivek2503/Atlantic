package com.example.zub.fuckgod;

public class Blog {


    private String title, Context, name, UserId, defaultImage, Usertime, hidden, Likes, ProfileId, HomeId;





    public Blog(){

    }



    public Blog(String title, String context, String name, String UserId, String defaultImage, String Usertime, String hidden, String Likes, String ProfileId, String HomeId) {
        this.title = title;
        Context = context;
        this.name = name;
        this.UserId =UserId;
        this.Usertime = Usertime;
        this.defaultImage= defaultImage;
        this.hidden = hidden;
        this.Likes =Likes;
        this.ProfileId =ProfileId;
        this.HomeId =HomeId;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return Context;
    }

    public void setContext(String context) {
        Context = context;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }


    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }

    public String getUsertime() {
        return Usertime;
    }

    public void setUsertime(String usertime) {
        Usertime = usertime;
    }

    public String getHidden() {
        return hidden;
    }

    public void setHidden(String hidden) {
        this.hidden = hidden;
    }

    public String getLikes() {
        return Likes;
    }

    public void setLikes(String likes) {
        Likes = likes;
    }

    public String getProfileId() {
        return ProfileId;
    }

    public void setProfileId(String profileId) {
        ProfileId = profileId;
    }

    public String getHomeId() {
        return HomeId;
    }

    public void setHomeId(String homeId) {
        HomeId = homeId;
    }
}
