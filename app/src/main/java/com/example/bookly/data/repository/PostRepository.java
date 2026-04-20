package com.example.bookly.data.repository;

import com.example.bookly.data.api.ApiService;
import com.example.bookly.data.api.RetrofitClient;
import com.example.bookly.data.model.BooksGenre;
import com.example.bookly.data.model.Genre;
import com.example.bookly.data.model.Post;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRepository {
    private final ApiService api;
    private final String apiKey;

    public interface Callback1<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public PostRepository() {
        api = RetrofitClient.getInstance().getApiService();
        apiKey = RetrofitClient.API_KEY;
    }

    public void getAllPosts(String token, Callback1<List<Post>> callback) {
        api.getPosts(
                apiKey,
                RetrofitClient.bearerToken(token),
                "*, user:users(*)",
                "id.desc"
        ).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Помилка завантаження: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getPostsByUser(int userId, String token, Callback1<List<Post>> callback) {
        api.getPostsByUser(apiKey, RetrofitClient.bearerToken(token),
                "eq." + userId, "*").enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Помилка: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createPost(Post post, String token, Callback1<Post> callback) {
        api.createPost(apiKey, RetrofitClient.bearerToken(token),
                "return=representation", post).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    callback.onError("Помилка створення оголошення: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updatePost(int id, Map<String, Object> updates, String token, Callback1<Void> callback) {
        api.updatePost(apiKey, RetrofitClient.bearerToken(token),
                "return=minimal", "eq." + id, updates).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError("Помилка оновлення: " + response.code());
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deletePost(int id, String token, Callback1<Void> callback) {
        api.deletePost(apiKey, RetrofitClient.bearerToken(token), "eq." + id)
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

    public void getGenres(String token, Callback1<List<Genre>> callback) {
        api.getGenres(apiKey, RetrofitClient.bearerToken(token))
                .enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Помилка завантаження жанрів");
                }
            }
            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void addBookGenre(int postId, int genreId, String token, Callback1<Void> callback) {
        api.addBookGenre(apiKey, RetrofitClient.bearerToken(token),
                new BooksGenre(postId, genreId)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError("Помилка додавання жанру");
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void deleteBookGenres(int postId, String token, Callback1<Void> callback) {
        api.deleteBookGenres(apiKey, RetrofitClient.bearerToken(token), "eq." + postId)
                .enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError("Помилка видалення жанрів");
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
