package com.example.bookly.domain.usecase;

import com.example.bookly.data.model.User;
import com.example.bookly.data.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class ProfileUseCase {
    private final UserRepository userRepository;

    // --- Інтерфейси для зворотного зв'язку ---
    public interface ProfileCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String message);
    }

    public ProfileUseCase() {
        userRepository = new UserRepository();
    }

    // Отримання даних користувача
    public void getUserById(int id, String token, ProfileCallback callback) {
        userRepository.getUserById(id, token, new UserRepository.Callback1<User>() {
            @Override public void onSuccess(User result) { callback.onSuccess(result); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    // Завантаження фото (Uri -> Bytes -> Storage)
    public void uploadPhoto(int userId, byte[] bytes, String token, UploadCallback callback) {
        String fileName = "avatar_" + userId + "_" + System.currentTimeMillis() + ".jpg";
        userRepository.uploadFile(fileName, bytes, token, new UserRepository.Callback1<String>() {
            @Override public void onSuccess(String result) { callback.onSuccess(result); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    // Оновлення профілю (9 аргументів: userId + 5 полів + photoUrl + token + callback)
    public void updateProfile(int userId, String email, String city, String region,
                              String district, String about, String photoUrl,
                              String token, UpdateCallback callback) {

        if (email != null && !email.isEmpty()) {
            String pattern = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
            if (!email.matches(pattern)) {
                callback.onError("Некоректний формат email");
                return;
            }
        }

        Map<String, Object> updates = new HashMap<>();
        if (email != null) updates.put("email", email);
        if (city != null) updates.put("city", city);
        if (region != null) updates.put("region", region);
        if (district != null) updates.put("district", district);
        if (about != null) updates.put("about", about);
        if (photoUrl != null) updates.put("photo_url", photoUrl);

        userRepository.updateUser(userId, updates, token, new UserRepository.Callback1<Void>() {
            @Override public void onSuccess(Void result) { callback.onSuccess(); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    // Видалення акаунту
    public void deleteAccount(int userId, String token, UpdateCallback callback) {
        userRepository.deleteUser(userId, token, new UserRepository.Callback1<Void>() {
            @Override public void onSuccess(Void result) { callback.onSuccess(); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }
}