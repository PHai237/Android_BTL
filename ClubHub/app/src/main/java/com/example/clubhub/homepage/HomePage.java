package com.example.clubhub.homepage;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clubhub.R;
import java.util.ArrayList;
import java.util.List;
import com.example.clubhub.profile.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        recyclerView = findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fake data
        postList = new ArrayList<>();
        postList.add(new Post(
                R.drawable.ic_club_logo_1, "Badminton",
                R.drawable.ic_user_avt_1, "Jane Cooper",
                "Lovely day to play badminton everyone",
                R.drawable.sample_img_1, "Comment: Ready for the game!"
        ));

        postList.add(new Post(
                R.drawable.ic_club_logo_2, "Football",
                R.drawable.ic_user_avt_2, "Alex Smith",
                "Kick-off at 7pm. Who's coming?",
                R.drawable.sample_img_2, "Comment: Count me in!"
        ));

        postList.add(new Post(
                R.drawable.ic_club_logo_3, "Chess",
                R.drawable.ic_user_avt_3, "Emily Wang",
                "Chess tournament this Sunday. Register now.",
                R.drawable.sample_img_3, "Comment: Can't wait for the challenge."
        ));

        postList.add(new Post(
                R.drawable.ic_club_logo_4, "Music",
                R.drawable.ic_user_avt_4, "Liam Nguyen",
                "Open mic night this Friday at the club lounge!",
                R.drawable.sample_img_4, "Comment: I'll bring my guitar."
        ));

        postList.add(new Post(
                R.drawable.ic_club_logo_5, "Basketball",
                R.drawable.ic_user_avt_5, "Sophia Lee",
                "Excited for our weekend practice session 🏀",
                R.drawable.sample_img_5, "Comment: Let's win this season!"
        ));

        postList.add(new Post(
                R.drawable.ic_club_logo_6, "Photography",
                R.drawable.ic_user_avt_6, "Michael Tran",
                "Sunset photo walk, anyone?",
                R.drawable.sample_img_6, "Comment: Great idea, I'm joining!"
        ));

        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                // Chuyển sang LoginActivity (ở package profile)
                Intent intent = new Intent(HomePage.this, LoginActivity.class);
                startActivity(intent);
                return true;
            }
            // Các tab khác nếu muốn xử lý thêm ở đây (nav_post, nav_club, nav_event)
            return false;
        });
    }

}
