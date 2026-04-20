package com.example.bookly.domain.usecase;

import com.example.bookly.data.model.User;
import com.example.bookly.data.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class ProfileUseCase {
    private final UserRepository userRepository;

    public interface ProfileCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onError(String message);
    }

    public ProfileUseCase() {
        userRepository = new UserRepository();
    }

    public void getUserById(int id, String token, ProfileCallback callback) {
        userRepository.getUserById(id, token, new UserRepository.Callback1<User>() {
            @Override public void onSuccess(User result) { callback.onSuccess(result); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    public void updateProfile(int userId, String email, String city, String region,
                              String district, String about, String token, UpdateCallback callback) {
        if (email != null && !email.isEmpty()) {
            String pattern = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
            if (!email.matches(pattern)) {
                callback.onError("Некоректний формат email");
                return;
            }
        }
        Map<String, Object> updates = new HashMap<>();
        if (email != null && !email.isEmpty()) updates.put("email", email);
        if (city != null) updates.put("city", city);
        if (region != null) updates.put("region", region);
        if (district != null) updates.put("district", district);
        if (about != null) updates.put("about", about);

        userRepository.updateUser(userId, updates, token, new UserRepository.Callback1<Void>() {
            @Override public void onSuccess(Void result) { callback.onSuccess(); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    public void deleteAccount(int userId, String token, UpdateCallback callback) {
        userRepository.deleteUser(userId, token, new UserRepository.Callback1<Void>() {
            @Override public void onSuccess(Void result) { callback.onSuccess(); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }
}
