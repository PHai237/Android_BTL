package com.example.clubhub.homepage;

public class Post {
    public int clubAvatarRes;
    public String clubName;
    public int userAvatarRes;
    public String userName;
    public String content;
    public int imageRes;

    public Post(int clubAvatarRes, String clubName, int userAvatarRes, String userName, String content, int imageRes, String s) {
        this.clubAvatarRes = clubAvatarRes;
        this.clubName = clubName;
        this.userAvatarRes = userAvatarRes;
        this.userName = userName;
        this.content = content;
        this.imageRes = imageRes;
    }
}
