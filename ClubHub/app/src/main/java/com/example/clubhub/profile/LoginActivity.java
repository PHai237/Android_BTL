package com.example.clubhub.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.clubhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextView tvErrorEmail, tvErrorPassword, tvSignUp;
    private MaterialButton btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogIn = findViewById(R.id.btnLogIn);

        // Tab SignUp
        tvSignUp = findViewById(R.id.tv_signup);

        // Error TextViews
        tvErrorEmail = findViewById(R.id.tvErrorEmail);
        tvErrorPassword = findViewById(R.id.tvErrorPassword);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        // Chuyển sang SignUpActivity khi bấm tab Sign Up
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        btnLogIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            // Reset error messages
            tvErrorEmail.setVisibility(View.GONE);
            tvErrorPassword.setVisibility(View.GONE);

            boolean valid = true;
            // Validate Email
            if (email.isEmpty()) {
                tvErrorEmail.setText("Email cannot be empty.");
                tvErrorEmail.setVisibility(View.VISIBLE);
                valid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tvErrorEmail.setText("Invalid email address. Missing '@' symbol.");
                tvErrorEmail.setVisibility(View.VISIBLE);
                valid = false;
            }

            // Validate Password
            if (password.isEmpty()) {
                tvErrorPassword.setText("Password cannot be empty.");
                tvErrorPassword.setVisibility(View.VISIBLE);
                valid = false;
            }

            if (!valid) return;

            // Kiểm tra tài khoản với Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(email).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String pwHash = documentSnapshot.getString("passwordHash");
                            if (pwHash != null && pwHash.equals(password)) {
                                // Đăng nhập thành công
                                String fullName = documentSnapshot.getString("fullName");
                                String phoneNumber = documentSnapshot.getString("phoneNumber");
                                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("name", fullName);
                                intent.putExtra("phone", phoneNumber);
                                startActivity(intent);
                                finish();
                            } else {
                                tvErrorPassword.setText("Incorrect password. Please try again.");
                                tvErrorPassword.setVisibility(View.VISIBLE);
                            }
                        } else {
                            tvErrorEmail.setText("Email not found. Please check your email or sign up.");
                            tvErrorEmail.setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
