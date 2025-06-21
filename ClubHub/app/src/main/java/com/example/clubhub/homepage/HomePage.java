package com.example.clubhub.homepage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clubhub.R;
import java.util.ArrayList;
import java.util.List;

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

        postList.add(new Post(
                R.drawable.ic_club_logo_7, "Book Club",
                R.drawable.ic_user_avt_7, "Anna Chen",
                "Next book: 'The Alchemist' by Paulo Coelho.",
                R.drawable.sample_img_7, "Comment: Already started reading!"
        ));

        postList.add(new Post(
                R.drawable.ic_club_logo_8, "Volleyball",
                R.drawable.ic_user_avt_8, "David Kim",
                "Friendly match at Central Park tomorrow morning.",
                R.drawable.sample_img_8, "Comment: Let's play hard!"
        ));

        postList.add(new Post(
                R.drawable.ic_club_logo_9, "Running",
                R.drawable.ic_user_avt_9, "Grace Hoang",
                "Morning run at 6am. Fresh air, good vibes!",
                R.drawable.sample_img_9, "Comment: I’ll set my alarm!"
        ));

        postList.add(new Post(
                R.drawable.ic_club_logo_10, "Coding",
                R.drawable.ic_user_avt_10, "Ben Pham",
                "Hackathon sign-up is open! Build, learn, win.",
                R.drawable.sample_img_10, "Comment: Joining for sure!"
        ));


        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);
    }
}
