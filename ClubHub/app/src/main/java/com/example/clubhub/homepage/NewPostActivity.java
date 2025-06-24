package com.example.clubhub.homepage;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.clubhub.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.*;

public class NewPostActivity extends AppCompatActivity {
    private Spinner spinnerClub;
    private EditText etContent, etImageUrl;
    private ImageView imgPreview;
    private Button btnFindImage, btnPost;
    private String email;

    // Temporarily store the clubs the user is a member of
    private List<Club> clubList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        spinnerClub = findViewById(R.id.spinner_club);
        etContent = findViewById(R.id.et_content);
        etImageUrl = findViewById(R.id.et_image_url);
        imgPreview = findViewById(R.id.img_preview);
        btnFindImage = findViewById(R.id.btn_find_image);
        btnPost = findViewById(R.id.btn_post);

        // Back button
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        email = getIntent().getStringExtra("email");

        // Preview image when the link is pasted
        etImageUrl.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                if (!url.isEmpty() && (url.startsWith("http://") || url.startsWith("https://"))) {
                    imgPreview.setVisibility(View.VISIBLE);
                    Glide.with(NewPostActivity.this).load(url).into(imgPreview);
                } else {
                    imgPreview.setVisibility(View.GONE);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnFindImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://unsplash.com/s/photos"));
            startActivity(intent);
        });

        // Check if the user has joined any clubs
        checkMembershipAndAllowPost();

        // Load the list of clubs the user has joined from Firestore
        loadUserClubs();

        btnPost.setOnClickListener(v -> {
            String content = etContent.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            // If no club is selected: Show popup and don't post
            if (clubList.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("You have not joined any clubs")
                        .setMessage("You need to join at least one club to post!")
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Explore Clubs", (dialog, which) -> {
                            // TODO: Open club exploration screen if needed
                            // startActivity(new Intent(this, ExploreClubActivity.class));
                        })
                        .show();
                return;
            }

            if (content.isEmpty()) {
                etContent.setError("Please enter content");
                return;
            }

            int selectedIdx = spinnerClub.getSelectedItemPosition();
            if (selectedIdx < 0) {
                Toast.makeText(this, "You need to select a club to post!", Toast.LENGTH_SHORT).show();
                return;
            }

            Club selectedClub = clubList.get(selectedIdx);
            uploadPost(content, imageUrl, selectedClub);
        });

    }

    private void checkMembershipAndAllowPost() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query ClubMembers collection using email as userId
        db.collection("ClubMembers")
                .whereEqualTo("email", email)  // Use email to search instead of userId
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // If no data found, the user has not joined any club
                        enablePostButton(false);
                        showErrorMessage("You have not joined any clubs.");
                    } else {
                        // The user has joined at least one club
                        enablePostButton(true);
                    }
                })
                .addOnFailureListener(e -> {
                    showErrorMessage("Error occurred while checking clubs.");
                    enablePostButton(false);
                });
    }

    // Show error message
    private void showErrorMessage(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // Enable or disable the post button
    private void enablePostButton(boolean enable) {
        btnPost.setEnabled(enable);
    }

    private void loadUserClubs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ClubMembers")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    clubList.clear();  // Clear list to ensure we load fresh data
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String clubId = doc.getString("clubId");
                        // Query additional club info from Clubs collection
                        db.collection("Clubs").document(clubId)
                                .get()
                                .addOnSuccessListener(clubDoc -> {
                                    String clubName = clubDoc.getString("name");  // Get club name from Firestore
                                    clubList.add(new Club(clubId, clubName));  // Add to the list

                                    // After all clubs are loaded, set the spinner
                                    if (clubList.size() > 0) {
                                        ArrayAdapter<Club> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clubList);
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerClub.setAdapter(adapter);
                                        enablePostButton(true);  // Enable the post button now that we have data
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    showErrorMessage("Error loading club data");
                                });
                    }

                    // After all clubs are loaded, set the spinner
                    if (clubList.isEmpty()) {
                        enablePostButton(false);  // Disable post button if no clubs found
                    }
                })
                .addOnFailureListener(e -> {
                    showErrorMessage("Error loading clubs");
                    enablePostButton(false);  // Disable post button on failure
                });
    }

    // Upload post
    private void uploadPost(String content, String imageUrl, Club club) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String postId = db.collection("posts").document().getId();

        Map<String, Object> post = new HashMap<>();
        post.put("postId", postId);
        post.put("authorEmail", email);
        post.put("content", content);
        post.put("imageUrl", imageUrl);
        post.put("createdAt", new Date());
        post.put("clubId", club.clubId);
        post.put("authorUserId", email);

        db.collection("posts").document(postId).set(post)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Post successful!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Post error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Club class
    private static class Club {
        String clubId, clubName;
        Club(String id, String name) {
            clubId = id; clubName = name;
        }
    }
}
