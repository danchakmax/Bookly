package com.example.bookly.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.data.model.User;
import com.example.bookly.domain.usecase.ProfileUseCase;

import java.io.InputStream;

public class EditProfileFragment extends Fragment {

    private EditText etEmail, etCity, etRegion, etDistrict, etAbout;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;
    private ImageView ivProfilePhoto;

    private ProfileUseCase profileUseCase;
    private SharedPrefsManager prefs;

    private String uploadedPhotoUrl = null;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    ivProfilePhoto.setImageURI(uri);
                    uploadImageToServer(uri);
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        etEmail    = view.findViewById(R.id.etEmail);
        etCity     = view.findViewById(R.id.etCity);
        etRegion   = view.findViewById(R.id.etRegion);
        etDistrict = view.findViewById(R.id.etDistrict);
        etAbout    = view.findViewById(R.id.etAbout);
        btnSave    = view.findViewById(R.id.btnSave);
        btnCancel  = view.findViewById(R.id.btnCancel);
        progressBar = view.findViewById(R.id.progressBar);

        prefs = SharedPrefsManager.getInstance(requireContext());
        profileUseCase = new ProfileUseCase();

        loadCurrentData();

        View btnChangePhoto = view.findViewById(R.id.btnChangePhoto);
        if (btnChangePhoto != null) {
            btnChangePhoto.setOnClickListener(v -> mGetContent.launch("image/*"));
        }

        btnSave.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void loadCurrentData() {
        progressBar.setVisibility(View.VISIBLE);
        profileUseCase.getUserById(prefs.getUserId(), prefs.getToken(),
                new ProfileUseCase.ProfileCallback() {
                    @Override
                    public void onSuccess(User user) {
                        if (getActivity() != null) getActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            etEmail.setText(user.getEmail());
                            etCity.setText(user.getCity() != null ? user.getCity() : "");
                            etRegion.setText(user.getRegion() != null ? user.getRegion() : "");
                            etDistrict.setText(user.getDistrict() != null ? user.getDistrict() : "");
                            etAbout.setText(user.getAbout() != null ? user.getAbout() : "");

                            uploadedPhotoUrl = user.getPhotoUrl();
                            if (uploadedPhotoUrl != null && !uploadedPhotoUrl.isEmpty()) {
                                Glide.with(EditProfileFragment.this)
                                        .load(uploadedPhotoUrl)
                                        .circleCrop()
                                        .into(ivProfilePhoto);
                            }
                        });
                    }
                    @Override public void onError(String message) {
                        if (getActivity() != null) getActivity().runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    }
                });
    }

    private void uploadImageToServer(Uri uri) {
        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();

            progressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);

            profileUseCase.uploadPhoto(prefs.getUserId(), bytes, prefs.getToken(), new ProfileUseCase.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    if (getActivity() != null) getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        uploadedPhotoUrl = imageUrl;
                        Toast.makeText(getContext(), "Фото готове", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String message) {
                    if (getActivity() != null) getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(), "Помилка завантаження: " + message, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveProfile() {
        String email    = etEmail.getText().toString().trim();
        String city     = etCity.getText().toString().trim();
        String region   = etRegion.getText().toString().trim();
        String district = etDistrict.getText().toString().trim();
        String about    = etAbout.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        profileUseCase.updateProfile(prefs.getUserId(), email, city, region, district, about,
                uploadedPhotoUrl, prefs.getToken(), new ProfileUseCase.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        if (getActivity() != null) getActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Профіль оновлено", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        });
                    }
                    @Override
                    public void onError(String message) {
                        if (getActivity() != null) getActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            btnSave.setEnabled(true);
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }
}