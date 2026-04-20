package com.example.bookly.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.bookly.R;
import com.example.bookly.ui.fragments.AdminComplaintsFragment;
import com.example.bookly.ui.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Адмін панель");

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavAdmin);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_complaints) {
                fragment = new AdminComplaintsFragment();
                setTitle("Скарги");
            } else if (id == R.id.nav_settings) {
                fragment = new SettingsFragment();
                setTitle("Налаштування");
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerAdmin, fragment)
                        .commit();
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerAdmin, new AdminComplaintsFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_complaints);
        }
    }
}
