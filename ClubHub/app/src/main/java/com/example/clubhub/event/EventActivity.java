package com.example.clubhub.event;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clubhub.R;
import com.example.clubhub.homepage.HomePage;
import com.example.clubhub.profile.LoginActivity;
import com.example.clubhub.profile.ProfileActivity;
import com.example.clubhub.club.ClubListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    private RecyclerView recyclerEvents;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private ImageButton btnAddEvent;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        db = FirebaseFirestore.getInstance();

        // Khởi tạo RecyclerView và Adapter
        recyclerEvents = findViewById(R.id.recycler_events);
        eventAdapter = new EventAdapter(this, new ArrayList<>());  // Pass an empty list initially
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerEvents.setAdapter(eventAdapter);

        // Khởi tạo button thêm sự kiện
        btnAddEvent = findViewById(R.id.btn_add_event);

        // Lấy dữ liệu sự kiện từ Firestore
        loadEvents();

        // Đọc email một lần khi vào màn hình
        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        email = prefs.getString("email", null);

        // Kiểm tra vai trò người dùng để hiển thị button thêm sự kiện
        checkUserRole();

        // Thiết lập sự kiện cho BottomNavigationView (theo cách của HomePage.java)
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Dùng if để kiểm tra ID item
                if (item.getItemId() == R.id.nav_post) {
                    // Chuyển đến HomePage
                    startActivity(new Intent(EventActivity.this, HomePage.class));
                    return true;

                } else if (item.getItemId() == R.id.nav_profile) {
                    // Chuyển đến ProfileActivity hoặc LoginActivity nếu chưa đăng nhập
                    if (email == null) {
                        startActivity(new Intent(EventActivity.this, LoginActivity.class));
                    } else {
                        Intent profileIntent = new Intent(EventActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("email", email);
                        startActivity(profileIntent);
                    }
                    return true;

                } else if (item.getItemId() == R.id.nav_event) {
                    // Ở lại EventActivity
                    return true;

                } else if (item.getItemId() == R.id.nav_club) {
                    // Điều hướng đến ClubListActivity
                    startActivity(new Intent(EventActivity.this, ClubListActivity.class));
                    return true;

                }

                return false;
            }
        });
    }

    private void loadEvents() {
        db.collection("events")  // Đảm bảo rằng tên bảng "events" đúng với Firestore của bạn
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Event> events = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Event event = document.toObject(Event.class);
                                events.add(event);
                            }
                            // Cập nhật danh sách sự kiện vào Adapter
                            eventAdapter.updateEvents(events);
                        } else {
                            Toast.makeText(EventActivity.this, "Error fetching events.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserRole() {
        // Kiểm tra xem người dùng có phải là chủ sở hữu của câu lạc bộ
        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        String userEmail = prefs.getString("email", null);

        if (userEmail != null) {
            db.collection("clubs")
                    .whereEqualTo("createdById", userEmail)  // So sánh với createdById thay vì owner
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            btnAddEvent.setVisibility(View.VISIBLE);  // Hiển thị nút thêm sự kiện
                        } else {
                            btnAddEvent.setVisibility(View.GONE);  // Ẩn nút nếu không phải chủ
                        }
                    });
        }
    }
}
