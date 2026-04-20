package com.example.bookly.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.data.model.Post;
import com.example.bookly.ui.fragments.ComplaintDialogFragment;
import com.google.gson.Gson;

public class PostDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_JSON = "post_json";

    private ImageView imgPhoto;
    private TextView tvTitle, tvAuthor, tvDealType, tvDescription, tvOwnerName, tvOwnerCity;
    private Button btnComplain, btnViewOwner;

    private Post post;
    private SharedPrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Оголошення");
        }

        imgPhoto     = findViewById(R.id.imgPostPhoto);
        tvTitle      = findViewById(R.id.tvPostTitle);
        tvAuthor     = findViewById(R.id.tvPostAuthor);
        tvDealType   = findViewById(R.id.tvPostDealType);
        tvDescription= findViewById(R.id.tvPostDescription);
        tvOwnerName  = findViewById(R.id.tvOwnerName);
        tvOwnerCity  = findViewById(R.id.tvOwnerCity);
        btnComplain  = findViewById(R.id.btnComplain);
        btnViewOwner = findViewById(R.id.btnViewOwner);

        prefs = SharedPrefsManager.getInstance(this);

        String postJson = getIntent().getStringExtra(EXTRA_POST_JSON);
        if (postJson != null) {
            post = new Gson().fromJson(postJson, Post.class);
            bindPost();
        }

        btnComplain.setOnClickListener(v -> {
            if (post != null) {
                ComplaintDialogFragment dialog = ComplaintDialogFragment.newInstance(post.getId());
                dialog.show(getSupportFragmentManager(), "complaint");
            }
        });

        btnViewOwner.setOnClickListener(v -> {
            if (post != null && post.getUser() != null) {
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.EXTRA_USER_ID, post.getUser().getId());
                startActivity(intent);
            }
        });

        // Hide complain button if own post
        if (post != null && post.getUserId() == prefs.getUserId()) {
            btnComplain.setVisibility(View.GONE);
        }
    }

    private void bindPost() {
        tvTitle.setText(post.getTitle());
        tvAuthor.setText("Автор: " + (post.getAuthor() != null ? post.getAuthor() : "—"));
        tvDealType.setText("Тип угоди: " + post.getDealTypeUkrainian());
        tvDescription.setText(post.getDescription() != null ? post.getDescription() : "");

        if (post.getUser() != null) {
            tvOwnerName.setText(post.getUser().getName());
            tvOwnerCity.setText(post.getUser().getCity() != null ? post.getUser().getCity() : "");
        }

        if (post.getPhotoUrl() != null && !post.getPhotoUrl().isEmpty()) {
            Glide.with(this).load(post.getPhotoUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(imgPhoto);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
