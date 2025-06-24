package com.example.clubhub.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.clubhub.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.*;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView imgAvatar;
    private EditText etEmail, etName, etPhone;
    private Button btnSave, btnChangeAvatar;
    private String email;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imgAvatar = findViewById(R.id.img_avatar);
        etEmail = findViewById(R.id.et_email);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        btnSave = findViewById(R.id.btn_save);
        btnChangeAvatar = findViewById(R.id.btn_change_avatar);

        email = getIntent().getStringExtra("email");
        loadUserProfile(email);

        etEmail.setEnabled(false); // Không cho sửa email

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Name required");
                return;
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(email)
                    .update("fullName", name, "phoneNumber", phone)
                    .addOnSuccessListener(aVoid -> showSuccessDialog("Profile updated successfully!"))
                    .addOnFailureListener(e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        btnChangeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

    }

    private void loadUserProfile(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        String phone = documentSnapshot.getString("phoneNumber");
                        String avatar = documentSnapshot.getString("photoUrl");
                        etEmail.setText(email);
                        etName.setText(name != null ? name : "");
                        etPhone.setText(phone != null ? phone : "");
                        if (avatar != null && !avatar.isEmpty()) {
                            File file = new File(avatar);
                            if (file.exists()) {
                                Glide.with(this).load(file).into(imgAvatar);
                            } else {
                                imgAvatar.setImageResource(R.drawable.ic_user_avt_default);
                            }
                        } else {
                            imgAvatar.setImageResource(R.drawable.ic_user_avt_default);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Copy file ảnh vào app folder và trả về absolute path
    private String copyImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "avatar_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Xử lý kết quả chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            String localPath = copyImageToInternalStorage(imageUri);
            if (localPath != null) {
                Glide.with(this).load(new File(localPath)).into(imgAvatar);
                // Lưu đường dẫn file này vào Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users").document(email)
                        .update("photoUrl", localPath)
                        .addOnSuccessListener(aVoid -> showSuccessDialog("Avatar updated!"))
                        .addOnFailureListener(e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Cannot copy image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Pop-up báo thành công
    private void showSuccessDialog(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.dialog_success, null);
        builder.setView(dialogView);

        android.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        ((android.widget.TextView) dialogView.findViewById(R.id.tvDialogMessage)).setText(message);

        dialogView.findViewById(R.id.btnDialogOk).setOnClickListener(v -> {
            dialog.dismiss();
            // Nếu muốn quay về ProfileActivity thì dùng finish();
             finish();
        });

        dialog.show();

        android.view.Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
