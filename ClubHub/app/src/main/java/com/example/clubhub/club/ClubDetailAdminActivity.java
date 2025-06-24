package com.example.clubhub.club;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import com.example.clubhub.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClubDetailAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail_admin);

        String clubId = getIntent().getStringExtra("clubId");
        if (clubId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy club", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Clubs").document(clubId);

        docRef.get().addOnSuccessListener(doc -> {
            if (!doc.exists()) {
                Toast.makeText(this, "Club không tồn tại!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Lấy dữ liệu từ Firestore
            String clubName = doc.getString("name");
            String clubDesc = doc.getString("description");
            String adminUserId = doc.getString("createdById");

            ((TextView) findViewById(R.id.tvClubName)).setText(clubName != null ? clubName : "No name");
            ((TextView) findViewById(R.id.tvClubDesc)).setText(clubDesc != null ? clubDesc : "No description");
            ((TextView) findViewById(R.id.tvClubMembers)).setText("120 members");

            // Lấy tên admin từ collection Users
            if (adminUserId != null) {
                DocumentReference adminRef = db.collection("Users").document(adminUserId);
                adminRef.get().addOnSuccessListener(adminDoc -> {
                    String adminName = adminDoc.getString("fullName");
                    ((TextView) findViewById(R.id.tvAdminName)).setText(adminName != null ? adminName : "Admin Name");
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể lấy tên admin", Toast.LENGTH_SHORT).show();
                });
            }

            // Xử lý sự kiện khi bấm vào nút "Edit"
            ImageView imgEdit = findViewById(R.id.imgEdit);
            imgEdit.setOnClickListener(v -> {
                showPopupMenu(v, clubId, clubName, clubDesc);
            });

            // Members mock
            LinearLayout membersLayout = findViewById(R.id.layoutMembers);
            membersLayout.removeAllViews();
            for (int i = 0; i < 3; i++) {
                ImageView img = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(64, 64);
                params.setMargins(0, 0, 16, 0);
                img.setLayoutParams(params);
                img.setImageResource(R.mipmap.ic_launcher);
                img.setBackgroundResource(R.drawable.bg_circle_avatar);
                membersLayout.addView(img);
            }

            // Events mock
            ((TextView) findViewById(R.id.tvEvent1)).setText("Event 1");
            ((TextView) findViewById(R.id.tvEvent2)).setText("Event 2");

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Không load được chi tiết club!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // Hiển thị PopupMenu khi bấm vào Edit
    private void showPopupMenu(View view, String clubId, String clubName, String clubDesc) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        // Inflate menu resource
        popupMenu.getMenuInflater().inflate(R.menu.edit_delete_menu, popupMenu.getMenu());

        // Xử lý sự kiện chọn mục trong menu
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();  // Lấy ID của item đã chọn
            if (itemId == R.id.menu_edit) {
                // Chuyển đến màn hình chỉnh sửa câu lạc bộ
                Intent intent = new Intent(ClubDetailAdminActivity.this, EditClubActivity.class); // Cập nhật từ ClubDetailActivity.this -> ClubDetailAdminActivity.this
                intent.putExtra("clubId", clubId);
                intent.putExtra("clubName", clubName);
                intent.putExtra("clubDesc", clubDesc);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_delete) {
                // Xóa câu lạc bộ
                deleteClub(clubId);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }


    // Hàm để xóa câu lạc bộ
// Hàm để xóa câu lạc bộ
    private void deleteClub(String clubId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference clubRef = db.collection("Clubs").document(clubId);

        // Xóa câu lạc bộ khỏi Firestore
        clubRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ClubDetailAdminActivity.this, "Xóa câu lạc bộ thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ClubDetailAdminActivity.this, "Không thể xóa câu lạc bộ!", Toast.LENGTH_SHORT).show();
                });
    }

}
