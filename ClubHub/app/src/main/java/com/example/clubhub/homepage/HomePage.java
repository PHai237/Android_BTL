package com.example.clubhub.homepage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clubhub.R;
import com.example.clubhub.club.ClubListActivity; // Thêm import cho ClubListActivity
import com.example.clubhub.event.EventActivity;
import com.example.clubhub.profile.LoginActivity;
import com.example.clubhub.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        recyclerView = findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        // Lấy dữ liệu thật từ Firestore
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            postList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Parse Firestore document thành Post object
                                Post post = document.toObject(Post.class);
                                postList.add(post);
                            }
                            postAdapter.notifyDataSetChanged();
                        } else {
                            // TODO: Xử lý lỗi, ví dụ: Toast thông báo không lấy được dữ liệu
                        }
                    }
                });

        // Đọc email một lần khi vào màn hình
        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        email = prefs.getString("email", null);  // Lưu trữ email trong biến toàn cục

        // Thiết lập sự kiện cho BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                Intent intent;
                if (email == null) {
                    // Chưa login, chuyển sang LoginActivity
                    intent = new Intent(HomePage.this, LoginActivity.class);
                } else {
                    // Đã login, chuyển sang ProfileActivity
                    intent = new Intent(HomePage.this, ProfileActivity.class);
                    intent.putExtra("email", email);
                }
                startActivity(intent);
                return true;
            }

            else if (item.getItemId() == R.id.nav_event) {
                if (email == null) {
                    // Nếu chưa đăng nhập, chuyển đến màn hình yêu cầu đăng nhập
                    Intent intent = new Intent(HomePage.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    // Người dùng đã đăng nhập, chuyển sang EventActivity
                    startActivity(new Intent(HomePage.this, EventActivity.class));
                }
                return true;
            }

            else if (item.getItemId() == R.id.nav_club) {
                // Điều hướng đến ClubListActivity
                Intent intent = new Intent(HomePage.this, ClubListActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });

        // Logic xử lý nút tạo bài đăng
        ImageButton btnCreatePost = findViewById(R.id.btn_create_post);
        if (email == null) {
            btnCreatePost.setVisibility(View.GONE); // Guest: ẩn nút
        } else {
            btnCreatePost.setVisibility(View.VISIBLE);
            btnCreatePost.setOnClickListener(v -> {
                Intent intent = new Intent(HomePage.this, NewPostActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            });
        }
    }
}
