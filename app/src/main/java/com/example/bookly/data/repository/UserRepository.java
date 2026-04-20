package com.example.bookly.data.repository;

import com.example.bookly.data.api.ApiService;
import com.example.bookly.data.api.RetrofitClient;
import com.example.bookly.data.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final ApiService api;
    private final String apiKey;

    public interface Callback1<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public UserRepository() {
        api = RetrofitClient.getInstance().getApiService();
        apiKey = RetrofitClient.API_KEY;
    }

    public void getUserByEmail(String email, String token, Callback1<User> callback) {
        api.getUsers(apiKey, RetrofitClient.bearerToken(token),
                "eq." + email, "*").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    callback.onError("Користувача не знайдено");
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getUserById(int id, String token, Callback1<User> callback) {
        api.getUserById(apiKey, RetrofitClient.bearerToken(token),
                "eq." + id, "*").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    callback.onError("Користувача не знайдено");
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createUser(User user, String token, Callback1<User> callback) {
        api.createUser(apiKey, RetrofitClient.bearerToken(token),
                "return=representation", user).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    callback.onError("Помилка реєстрації: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateUser(int id, Map<String, Object> updates, String token, Callback1<Void> callback) {
        api.updateUser(apiKey, RetrofitClient.bearerToken(token),
                "return=minimal", "eq." + id, updates).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Помилка оновлення: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteUser(int id, String token, Callback1<Void> callback) {
        api.deleteUser(apiKey, RetrofitClient.bearerToken(token), "eq." + id)
                .enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError("Помилка видалення: " + response.code());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
