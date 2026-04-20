package com.example.bookly.data.repository;

import com.example.bookly.data.api.ApiService;
import com.example.bookly.data.api.RetrofitClient;
import com.example.bookly.data.model.Complaint;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintRepository {

    private final ApiService api;
    private final String apiKey;

    public interface Callback1<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public ComplaintRepository() {
        api = RetrofitClient.getInstance().getApiService();
        apiKey = RetrofitClient.API_KEY;
    }

    public void getAllComplaints(String token, Callback1<List<Complaint>> callback) {
        api.getComplaints(
                apiKey,
                RetrofitClient.bearerToken(token),
                "*, post:posts(*), user:users(*)"
        ).enqueue(new Callback<List<Complaint>>() {
            @Override
            public void onResponse(Call<List<Complaint>> call, Response<List<Complaint>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Помилка завантаження скарг: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Complaint>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createComplaint(Complaint complaint, String token, Callback1<Complaint> callback) {
        api.createComplaint(
                apiKey,
                RetrofitClient.bearerToken(token),
                "return=representation",
                complaint
        ).enqueue(new Callback<List<Complaint>>() {
            @Override
            public void onResponse(Call<List<Complaint>> call, Response<List<Complaint>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    callback.onError("Помилка надсилання скарги: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Complaint>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteComplaint(int id, String token, Callback1<Void> callback) {
        api.deleteComplaint(
                apiKey,
                RetrofitClient.bearerToken(token),
                "eq." + id
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Помилка: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}