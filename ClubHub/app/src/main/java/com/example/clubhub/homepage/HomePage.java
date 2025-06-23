package com.example.clubhub.homepage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clubhub.profile.LoginActivity; // import LoginActivity ở package profile
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.clubhub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        recyclerView = findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
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
        // Thiết lập sự kiện cho BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
                String email = prefs.getString("email", null);
                Intent intent;
                if (email == null) {
                    // Chưa login, chuyển sang LoginActivity
                    intent = new Intent(HomePage.this, LoginActivity.class);
                } else {
                    // Đã login, chuyển sang ProfileActivity
                    intent = new Intent(HomePage.this, com.example.clubhub.profile.ProfileActivity.class);
                    intent.putExtra("email", email);
                }
                startActivity(intent);
                return true;
            }
            // Xử lý tab khác ở đây nếu có
            return false;
        });

    }
}
