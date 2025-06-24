package com.example.clubhub.club;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clubhub.R;
import com.example.clubhub.profile.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ClubListActivity extends AppCompatActivity {
    private RecyclerView rvClubs;
    private ClubAdapter clubAdapter;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_list);

        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        rvClubs = findViewById(R.id.recycler_clubs);
        rvClubs.setLayoutManager(new LinearLayoutManager(this));

        // Setup BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_club);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_club) return true;
            if (itemId == R.id.nav_post) {
                Intent intent = new Intent(this, com.example.clubhub.homepage.HomePage.class);
                startActivity(intent);
                finish();
                return true;
            }
            if (itemId == R.id.nav_profile) {
                String emailPref = prefs.getString("email", null);
                Intent intent;
                if (emailPref == null) {
                    intent = new Intent(this, com.example.clubhub.profile.LoginActivity.class);
                } else {
                    intent = new Intent(this, com.example.clubhub.profile.ProfileActivity.class);
                    intent.putExtra("email", emailPref);
                }
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClubList();
    }

    private void loadClubList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Clubs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Club> yourClubs = new ArrayList<>();
                    List<Club> recommendClubs = new ArrayList<>();
                    List<Club> joinedClubs = new ArrayList<>();
                    List<ClubListItem> items = new ArrayList<>();

                    for (var doc : queryDocumentSnapshots) {
                        Club club = Club.fromFirestore(doc);
                        boolean isJoined = checkIfUserJoinedClub(club.getId());

                        if (club.getCreatedById() != null && club.getCreatedById().equals(userId)) {
                            yourClubs.add(club);
                        } else if (isJoined) {
                            joinedClubs.add(club);
                        } else {
                            recommendClubs.add(club);
                        }
                    }

                    if (!yourClubs.isEmpty()) {
                        items.add(new ClubListItem(ClubListItem.TYPE_HEADER, "Your Club", null));
                        for (Club club : yourClubs) {
                            items.add(new ClubListItem(ClubListItem.TYPE_CLUB, null, club));
                        }
                    }

                    if (!joinedClubs.isEmpty()) {
                        items.add(new ClubListItem(ClubListItem.TYPE_HEADER, "Joined Club", null));
                        for (Club club : joinedClubs) {
                            items.add(new ClubListItem(ClubListItem.TYPE_CLUB, null, club));
                        }
                    }

                    if (!recommendClubs.isEmpty()) {
                        items.add(new ClubListItem(ClubListItem.TYPE_HEADER, "Recommend Club", null));
                        for (Club club : recommendClubs) {
                            items.add(new ClubListItem(ClubListItem.TYPE_CLUB, null, club));
                        }
                    }

                    clubAdapter = new ClubAdapter(items, club -> {
                        if (userId == null) {
                            Toast.makeText(ClubListActivity.this, "Bạn cần đăng nhập để xem chi tiết câu lạc bộ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ClubListActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            checkIfAdminAndNavigate(club);
                        }
                    });
                    rvClubs.setAdapter(clubAdapter);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Không tải được danh sách club", Toast.LENGTH_SHORT).show());
    }

    private boolean checkIfUserJoinedClub(String clubId) {
        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        return prefs.getBoolean("isJoined_" + clubId, false);
    }

    private void checkIfAdminAndNavigate(Club club) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String clubId = club.getId();
        db.collection("UserRoles")
                .whereEqualTo("clubID", clubId)
                .whereEqualTo("userId", userId)
                .whereEqualTo("roleId", "adminCLB")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Intent intent = new Intent(ClubListActivity.this, ClubDetailAdminActivity.class);
                        intent.putExtra("clubId", club.getId());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ClubListActivity.this, ClubDetailMemberActivity.class);
                        intent.putExtra("clubId", club.getId());
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ClubListActivity.this, "Không thể kiểm tra quyền truy cập", Toast.LENGTH_SHORT).show());
    }
}
