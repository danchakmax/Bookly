package com.example.bookly.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.domain.usecase.ProfileUseCase;
import com.example.bookly.ui.activities.LoginActivity;

public class SettingsFragment extends Fragment {

    private Button btnLogout, btnDeleteAccount;
    private SharedPrefsManager prefs;
    private ProfileUseCase profileUseCase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        btnLogout        = view.findViewById(R.id.btnLogout);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);

        prefs = SharedPrefsManager.getInstance(requireContext());
        profileUseCase = new ProfileUseCase();

        btnLogout.setOnClickListener(v -> confirmLogout());
        btnDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());

        return view;
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Вихід")
                .setMessage("Ви дійсно хочете вийти?")
                .setPositiveButton("Вийти", (d, w) -> logout())
                .setNegativeButton("Скасувати", null)
                .show();
    }

    private void logout() {
        prefs.clearSession();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Видалити акаунт")
                .setMessage("Видалити акаунт назавжди? Всі ваші дані буде втрачено.")
                .setPositiveButton("Видалити", (d, w) -> deleteAccount())
                .setNegativeButton("Скасувати", null)
                .show();
    }

    private void deleteAccount() {
        profileUseCase.deleteAccount(prefs.getUserId(), prefs.getToken(),
                new ProfileUseCase.UpdateCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    prefs.clearSession();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            }
            @Override
            public void onError(String message) {
                if (getActivity() != null) getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Помилка: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
