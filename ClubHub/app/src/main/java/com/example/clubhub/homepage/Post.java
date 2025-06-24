package com.example.clubhub.homepage;

public class Post {
    public String postId;         // ID bài viết
    public String clubAvatarUrl;  // URL ảnh club
    public String clubName;
    public String userAvatarUrl;  // URL ảnh user
    public String userName;
    public String content;
    public String imageUrl;       // URL ảnh post

    // Cần constructor rỗng để Firestore map dữ liệu vào object
    public Post() {}

    public Post(String postId, String clubAvatarUrl, String clubName,
                String userAvatarUrl, String userName, String content, String imageUrl) {
        this.postId = postId;
        this.clubAvatarUrl = clubAvatarUrl;
        this.clubName = clubName;
        this.userAvatarUrl = userAvatarUrl;
        this.userName = userName;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public String getPostId() { return postId; }
}
