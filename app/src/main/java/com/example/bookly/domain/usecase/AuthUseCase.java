package com.example.bookly.domain.usecase;

import com.example.bookly.data.model.User;
import com.example.bookly.data.repository.UserRepository;

import java.util.regex.Pattern;

public class AuthUseCase {
    private final UserRepository userRepository;

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public AuthUseCase() {
        userRepository = new UserRepository();
    }

    public void login(String email, String password, String token, AuthCallback callback) {
        String emailError = validateEmail(email);
        if (emailError != null) { callback.onError(emailError); return; }
        if (password.isEmpty()) { callback.onError("Введіть пароль"); return; }

        userRepository.getUserByEmail(email, token, new UserRepository.Callback1<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.getPassword().equals(password)) {
                    callback.onSuccess(user);
                } else {
                    callback.onError("Неправильний пароль");
                }
            }
            @Override
            public void onError(String message) {
                callback.onError("Користувача з таким email не знайдено");
            }
        });
    }

    public void register(String name, String email, String phone, String password,
                         String region, String district, String city,
                         String token, AuthCallback callback) {
        if (name.isEmpty()) { callback.onError("Введіть ім'я"); return; }
        String emailError = validateEmail(email);
        if (emailError != null) { callback.onError(emailError); return; }
        String phoneError = validatePhone(phone);
        if (phoneError != null) { callback.onError(phoneError); return; }
        if (password.length() < 6) { callback.onError("Пароль має містити мінімум 6 символів"); return; }

        User user = new User(name, email, phone, password, region, district, city);

        userRepository.createUser(user, token, new UserRepository.Callback1<User>() {
            @Override
            public void onSuccess(User result) {
                callback.onSuccess(result);
            }
            @Override
            public void onError(String message) {
                callback.onError("Помилка реєстрації: " + message);
            }
        });
    }

    public String validateEmail(String email) {
        if (email.isEmpty()) return "Введіть email";
        String pattern = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";
        if (!Pattern.matches(pattern, email)) return "Некоректний формат email";
        return null;
    }

    public String validatePhone(String phone) {
        if (phone.isEmpty()) return "Введіть номер телефону";
        String cleaned = phone.replaceAll("[\\s\\-\\(\\)]", "");
        if (!cleaned.matches("^(\\+38)?0\\d{9}$")) return "Некоректний номер телефону";
        return null;
    }
}
