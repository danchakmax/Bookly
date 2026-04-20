package com.example.bookly.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.bookly.R;
import com.example.bookly.ui.fragments.AddPostFragment;
import com.example.bookly.ui.fragments.HomeFragment;
import com.example.bookly.ui.fragments.MyBooksFragment;
import com.example.bookly.ui.fragments.ProfileFragment;
import com.example.bookly.ui.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
                setTitle("Bookly");
            } else if (id == R.id.nav_my_books) {
                fragment = new MyBooksFragment();
                setTitle("Мої книги");
            } else if (id == R.id.nav_add) {
                fragment = new AddPostFragment();
                setTitle("Додати оголошення");
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
                setTitle("Мій профіль");
            } else if (id == R.id.nav_settings) {
                fragment = new SettingsFragment();
                setTitle("Налаштування");
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new HomeFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            // Handled in HomeFragment via SearchView
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
