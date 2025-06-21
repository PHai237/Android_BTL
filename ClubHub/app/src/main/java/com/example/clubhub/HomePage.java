package com.example.clubhub;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class HomePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        postList.add(new Post("Badminton", "Jane Cooper", "Lovely day to play badminton everyone", R.drawable.sun));
        postList.add(new Post("Football", "Jane Cooper", "Lorem ipsum dolor sit amet", 0));

        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);
    }
}
