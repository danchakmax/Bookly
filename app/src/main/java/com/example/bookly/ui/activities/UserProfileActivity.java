package com.example.bookly.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.data.model.Post;
import com.example.bookly.data.model.User;
import com.example.bookly.domain.usecase.PostUseCase;
import com.example.bookly.domain.usecase.ProfileUseCase;
import com.example.bookly.ui.adapters.PostAdapter;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "user_id";

    private ImageView imgAvatar;
    private TextView tvName, tvEmail, tvCity, tvAbout;
    private RecyclerView rvBooks;
    private ProgressBar progressBar;

    private ProfileUseCase profileUseCase;
    private PostUseCase postUseCase;
    private SharedPrefsManager prefs;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Профіль");
        }

        imgAvatar   = findViewById(R.id.imgAvatar);
        tvName      = findViewById(R.id.tvName);
        tvEmail     = findViewById(R.id.tvEmail);
        tvCity      = findViewById(R.id.tvCity);
        tvAbout     = findViewById(R.id.tvAbout);
        rvBooks     = findViewById(R.id.rvUserBooks);
        progressBar = findViewById(R.id.progressBar);

        prefs = SharedPrefsManager.getInstance(this);
        profileUseCase = new ProfileUseCase();
        postUseCase = new PostUseCase();

        adapter = new PostAdapter(new PostAdapter.OnPostClickListener() {
            @Override public void onPostClick(Post post) {}
            @Override public void onPostEdit(Post post) {}
            @Override public void onPostDelete(Post post) {}
            @Override public void onPostComplain(Post post) {}
        }, false);

        rvBooks.setLayoutManager(new GridLayoutManager(this, 2));
        rvBooks.setAdapter(adapter);

        int userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        if (userId != -1) {
            loadUser(userId);
            loadUserPosts(userId);
        }
    }

    private void loadUser(int userId) {
        progressBar.setVisibility(View.VISIBLE);
        profileUseCase.getUserById(userId, prefs.getToken(), new ProfileUseCase.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvName.setText(user.getName());
                    tvEmail.setText(user.getEmail());
                    tvCity.setText(user.getCity() != null ? user.getCity() : "");
                    tvAbout.setText(user.getAbout() != null ? user.getAbout() : "");
                    if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                        Glide.with(UserProfileActivity.this)
                                .load(user.getPhotoUrl())
                                .circleCrop()
                                .into(imgAvatar);
                    }
                });
            }
            @Override
            public void onError(String message) {
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }
        });
    }

    private void loadUserPosts(int userId) {
        postUseCase.getMyPosts(userId, prefs.getToken(), new PostUseCase.PostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                runOnUiThread(() -> adapter.setPosts(posts));
            }
            @Override public void onError(String message) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
