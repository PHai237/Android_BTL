package com.example.clubhub.club;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clubhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ClubListActivity extends AppCompatActivity {
    private RecyclerView rvClubs;
    private ClubAdapter clubAdapter;
    private FrameLayout btnAddClub;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_list);

        SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        btnAddClub = findViewById(R.id.btn_add_club);
        if (userId == null) {
            btnAddClub.setVisibility(View.GONE);
        } else {
            btnAddClub.setVisibility(View.VISIBLE);
            btnAddClub.setOnClickListener(v -> {
                Intent intent = new Intent(ClubListActivity.this, CreateClubActivity.class);
                startActivity(intent);
            });
        }

        rvClubs = findViewById(R.id.recycler_clubs);
        rvClubs.setLayoutManager(new LinearLayoutManager(this));
        // KHÔNG GÁN clubAdapter ở đây nữa!
        // Adapter sẽ được set sau khi load xong data ở loadClubList()

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
                    List<ClubListItem> items = new ArrayList<>();

                    for (var doc : queryDocumentSnapshots) {
                        Club club = Club.fromFirestore(doc);
                        if (club.getCreatedById() != null && club.getCreatedById().equals(userId)) {
                            yourClubs.add(club);
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
                    if (!recommendClubs.isEmpty()) {
                        items.add(new ClubListItem(ClubListItem.TYPE_HEADER, "Recommend Club", null));
                        for (Club club : recommendClubs) {
                            items.add(new ClubListItem(ClubListItem.TYPE_CLUB, null, club));
                        }
                    }

                    clubAdapter = new ClubAdapter(items, club -> {
                        Intent intent = new Intent(ClubListActivity.this, ClubDetailActivity.class);
                        intent.putExtra("clubId", club.getId());
                        startActivity(intent);
                    });
                    rvClubs.setAdapter(clubAdapter);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Không tải được danh sách club", Toast.LENGTH_SHORT).show());
    }

}
