package com.example.clubhub.homepage;

public class Post {
    public String postId;         // ID bài viết
    public String clubId;         // ID câu lạc bộ (để liên kết bài viết với câu lạc bộ)
    public String clubAvatarUrl;  // URL ảnh club
    public String clubName;       // Tên câu lạc bộ
    public String userAvatarUrl;  // URL ảnh user
    public String userName;       // Tên user (tác giả bài viết)
    public String content;        // Nội dung bài viết
    public String imageUrl;       // URL ảnh đính kèm trong bài viết

    // Constructor rỗng để Firestore có thể map dữ liệu vào đối tượng
    public Post() {}

    // Constructor với các tham số
    public Post(String postId, String clubId, String clubAvatarUrl, String clubName,
                String userAvatarUrl, String userName, String content, String imageUrl) {
        this.postId = postId;
        this.clubId = clubId;               // Lưu ID câu lạc bộ
        this.clubAvatarUrl = clubAvatarUrl;
        this.clubName = clubName;
        this.userAvatarUrl = userAvatarUrl;
        this.userName = userName;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    // Getter và Setter (nếu cần)
    public String getPostId() { return postId; }
    public String getClubId() { return clubId; } // Getter cho clubId
    public String getClubAvatarUrl() { return clubAvatarUrl; }
    public String getClubName() { return clubName; }
    public String getUserAvatarUrl() { return userAvatarUrl; }
    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
}
