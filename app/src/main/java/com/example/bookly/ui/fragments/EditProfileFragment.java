package com.example.bookly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.data.model.User;
import com.example.bookly.domain.usecase.ProfileUseCase;

public class EditProfileFragment extends Fragment {

    private EditText etEmail, etCity, etRegion, etDistrict, etAbout;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;

    private ProfileUseCase profileUseCase;
    private SharedPrefsManager prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

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

        btnSave.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void loadCurrentData() {
        profileUseCase.getUserById(prefs.getUserId(), prefs.getToken(),
                new ProfileUseCase.ProfileCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    etEmail.setText(user.getEmail());
                    etCity.setText(user.getCity() != null ? user.getCity() : "");
                    etRegion.setText(user.getRegion() != null ? user.getRegion() : "");
                    etDistrict.setText(user.getDistrict() != null ? user.getDistrict() : "");
                    etAbout.setText(user.getAbout() != null ? user.getAbout() : "");
                });
            }
            @Override public void onError(String message) {}
        });
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
                prefs.getToken(), new ProfileUseCase.UpdateCallback() {
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
