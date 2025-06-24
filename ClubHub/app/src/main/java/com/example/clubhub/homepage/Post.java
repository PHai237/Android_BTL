package com.example.clubhub.homepage;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Post {
    public String clubAvatarUrl; // URL ảnh club (String)
    public String clubName;
    public String userAvatarUrl; // URL ảnh user (String)
    public String userName;
    public String content;
    public String imageUrl; // URL ảnh post (String)
    public String comment;  // nếu cần

    // Cần constructor rỗng để Firestore map dữ liệu vào object
    public Post() {}

    public Post(String clubAvatarUrl, String clubName, String userAvatarUrl, String userName, String content, String imageUrl, String comment) {
        this.clubAvatarUrl = clubAvatarUrl;
        this.clubName = clubName;
        this.userAvatarUrl = userAvatarUrl;
        this.userName = userName;
        this.content = content;
        this.imageUrl = imageUrl;
        this.comment = comment;
    }
}
