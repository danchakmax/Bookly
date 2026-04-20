package com.example.bookly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide; // Не забудь додати імпорт
import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.data.model.User;
import com.example.bookly.domain.usecase.ProfileUseCase;

public class ProfileFragment extends Fragment {

    private ImageView imgAvatar;
    private TextView tvName, tvEmail, tvCity, tvAbout;
    private Button btnEdit;

    private ProfileUseCase profileUseCase;
    private SharedPrefsManager prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvName    = view.findViewById(R.id.tvName);
        tvEmail   = view.findViewById(R.id.tvEmail);
        tvCity    = view.findViewById(R.id.tvCity);
        tvAbout   = view.findViewById(R.id.tvAbout);
        btnEdit   = view.findViewById(R.id.btnEditProfile);

        prefs = SharedPrefsManager.getInstance(requireContext());
        profileUseCase = new ProfileUseCase();

        loadProfile();

        btnEdit.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new EditProfileFragment())
                        .addToBackStack(null)
                        .commit());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        profileUseCase.getUserById(prefs.getUserId(), prefs.getToken(),
                new ProfileUseCase.ProfileCallback() {
                    @Override
                    public void onSuccess(User user) {
                        if (getActivity() != null) getActivity().runOnUiThread(() -> {
                            tvName.setText(user.getName());
                            tvEmail.setText(user.getEmail());
                            tvCity.setText(user.getCity() != null ? user.getCity() : "");
                            tvAbout.setText(user.getAbout() != null ? user.getAbout() : "");

                            String photoUrl = user.getPhotoUrl();
                            if (photoUrl != null && !photoUrl.isEmpty()) {
                                Glide.with(ProfileFragment.this)
                                        .load(photoUrl)
                                        .circleCrop() // Робить фото круглим
                                        .placeholder(R.drawable.ic_book_placeholder)
                                        .error(R.drawable.ic_book_placeholder)
                                        .into(imgAvatar);
                            } else {
                                imgAvatar.setImageResource(R.drawable.ic_book_placeholder);
                            }
                        });
                    }
                    @Override public void onError(String message) {
                    }
                });
    }
}

