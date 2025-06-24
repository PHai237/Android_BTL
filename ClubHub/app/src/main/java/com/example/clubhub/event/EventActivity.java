package com.example.clubhub.event;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clubhub.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends AppCompatActivity {

    private RecyclerView recyclerEvents;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
    private ImageButton btnAddEvent;

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

        // Kiểm tra vai trò người dùng để hiển thị button thêm sự kiện
        checkUserRole();
    }

    private void loadEvents() {
        db.collection("events")  // Đảm bảo rằng tên bảng "events" đúng với Firestore của bạn
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> events = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển DocumentSnapshot thành đối tượng Event
                            Event event = document.toObject(Event.class);
                            events.add(event);
                        }
                        // Cập nhật danh sách sự kiện vào Adapter
                        eventAdapter.updateEvents(events);
                    } else {
                        Toast.makeText(EventActivity.this, "Error fetching events.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole() {
        // Lấy thông tin email của người dùng từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        String userEmail = prefs.getString("email", null);

        // Kiểm tra xem người dùng đã đăng nhập chưa và có phải chủ sở hữu của club không
        if (userEmail != null) {
            // Kiểm tra vai trò chủ sở hữu club trong Firestore
            db.collection("clubs")
                    .whereEqualTo("createdById", userEmail)  // So sánh với createdById thay vì owner
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Nếu người dùng là chủ sở hữu của club, hiển thị nút "Thêm sự kiện"
                            btnAddEvent.setVisibility(View.VISIBLE);
                        } else {
                            // Nếu người dùng không phải chủ sở hữu, ẩn nút "Thêm sự kiện"
                            btnAddEvent.setVisibility(View.GONE);
                        }
                    });

        }
    }
}
