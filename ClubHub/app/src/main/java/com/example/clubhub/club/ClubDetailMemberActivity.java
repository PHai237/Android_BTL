package com.example.clubhub.club;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clubhub.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClubDetailMemberActivity extends AppCompatActivity {

    private String clubId;
    private TextView tvClubName, tvClubDesc, tvClubMembers, tvEvent1, tvEvent2, tvAdminName, btnJoinClub;
    private LinearLayout layoutMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail_member);

        clubId = getIntent().getStringExtra("clubId");
        if (clubId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy câu lạc bộ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo các view
        tvClubName = findViewById(R.id.tvClubName);
        tvClubDesc = findViewById(R.id.tvClubDesc);
        tvClubMembers = findViewById(R.id.tvClubMembers);
        tvEvent1 = findViewById(R.id.tvEvent1);
        tvEvent2 = findViewById(R.id.tvEvent2);
        layoutMembers = findViewById(R.id.layoutMembers);
        tvAdminName = findViewById(R.id.tvAdminName); // Thêm TextView cho tên admin
        btnJoinClub = findViewById(R.id.btnJoinClub);  // Lấy TextView cho Join Club

        // Lấy thông tin câu lạc bộ từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Clubs").document(clubId);
        docRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String clubName = doc.getString("name");
                String clubDesc = doc.getString("description");
                String adminUserId = doc.getString("createdById"); // Lấy adminUserId

                tvClubName.setText(clubName != null ? clubName : "No name");
                tvClubDesc.setText(clubDesc != null ? clubDesc : "No description");

                // Hiển thị số lượng thành viên
                tvClubMembers.setText("120 members"); // Có thể thay đổi sau khi kết nối thành viên thật sự

                // Lấy tên admin từ Firestore và hiển thị
                if (adminUserId != null) {
                    DocumentReference adminRef = db.collection("Users").document(adminUserId);
                    adminRef.get().addOnSuccessListener(adminDoc -> {
                        String adminName = adminDoc.getString("fullName");
                        // Cập nhật tên admin trong TextView
                        tvAdminName.setText(adminName != null ? adminName : "No admin");
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Không thể lấy tên admin", Toast.LENGTH_SHORT).show();
                    });
                }

                // Thành viên mock
                layoutMembers.removeAllViews();
                for (int i = 0; i < 3; i++) {
                    ImageView img = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(64, 64);
                    params.setMargins(0, 0, 16, 0);
                    img.setLayoutParams(params);
                    img.setImageResource(R.mipmap.ic_launcher);
                    img.setBackgroundResource(R.drawable.bg_circle_avatar);
                    layoutMembers.addView(img);
                }

                // Các sự kiện mock
                tvEvent1.setText("Event 1");
                tvEvent2.setText("Event 2");

                // Kiểm tra xem người dùng đã tham gia câu lạc bộ hay chưa
                checkIfUserJoinedClub();

            } else {
                Toast.makeText(this, "Không tìm thấy câu lạc bộ", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Không thể tải thông tin câu lạc bộ", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Xử lý sự kiện khi bấm vào nút "Join Club"
        btnJoinClub.setOnClickListener(v -> {
            // Thay đổi trạng thái nút và cập nhật dữ liệu Firestore
            toggleJoinLeaveClub();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại trạng thái nút mỗi khi quay lại activity
        checkIfUserJoinedClub();
    }

    private void checkIfUserJoinedClub() {
        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        boolean isJoined = prefs.getBoolean("isJoined_" + clubId, false);

        if (isJoined) {
            // Nếu người dùng đã tham gia, thay đổi nút thành "Out Club" và màu xám
            btnJoinClub.setText("Out Club");
            btnJoinClub.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_out_color))); // Màu xám
        } else {
            // Nếu người dùng chưa tham gia, vẫn giữ nút là "Join Club" với màu xanh
            btnJoinClub.setText("Join Club");
            btnJoinClub.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_join_color))); // Màu xanh
        }
    }

    private void toggleJoinLeaveClub() {
        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        boolean isJoined = btnJoinClub.getText().toString().equals("Join Club");

        if (isJoined) {
            // Tham gia câu lạc bộ
            btnJoinClub.setText("Out Club");
            btnJoinClub.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_out_color))); // Màu xám
            Toast.makeText(this, "Đã tham gia câu lạc bộ", Toast.LENGTH_SHORT).show();

            // Lưu trạng thái tham gia câu lạc bộ vào SharedPreferences
            prefs.edit().putBoolean("isJoined_" + clubId, true).apply();

        } else {
            // Rời khỏi câu lạc bộ
            btnJoinClub.setText("Join Club");
            btnJoinClub.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_join_color))); // Màu xanh
            Toast.makeText(this, "Đã rời khỏi câu lạc bộ", Toast.LENGTH_SHORT).show();

            // Lưu trạng thái tham gia câu lạc bộ vào SharedPreferences
            prefs.edit().putBoolean("isJoined_" + clubId, false).apply();
        }
    }
}
