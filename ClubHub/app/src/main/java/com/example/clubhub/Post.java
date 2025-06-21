package com.example.clubhub;


public class Post {
    public String clubName;
    public String author;
    public String content;
    public int imageResId;

    public Post(String clubName, String author, String content, int imageResId) {
        this.clubName = clubName;
        this.author = author;
        this.content = content;
        this.imageResId = imageResId;
    }
}