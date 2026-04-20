package com.example.bookly.domain.usecase;

import com.example.bookly.data.model.Genre;
import com.example.bookly.data.model.Post;
import com.example.bookly.data.repository.PostRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostUseCase {
    private final PostRepository postRepository;

    public interface PostCallback {
        void onSuccess(Post post);
        void onError(String message);
    }

    public interface PostsCallback {
        void onSuccess(List<Post> posts);
        void onError(String message);
    }

    public interface GenresCallback {
        void onSuccess(List<Genre> genres);
        void onError(String message);
    }

    public PostUseCase() {
        postRepository = new PostRepository();
    }

    public void getAllPosts(String token, PostsCallback callback) {
        postRepository.getAllPosts(token, new PostRepository.Callback1<List<Post>>() {
            @Override public void onSuccess(List<Post> result) { callback.onSuccess(result); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    public void getMyPosts(int userId, String token, PostsCallback callback) {
        postRepository.getPostsByUser(userId, token, new PostRepository.Callback1<List<Post>>() {
            @Override public void onSuccess(List<Post> result) { callback.onSuccess(result); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    public void createPost(int userId, String title, String author, String dealType,
                           String description, String photoUrl, int genreId,
                           String token, PostCallback callback) {
        String error = validatePost(title, author, dealType);
        if (error != null) { callback.onError(error); return; }

        Post post = new Post(userId, title, author, dealType, description, photoUrl);
        postRepository.createPost(post, token, new PostRepository.Callback1<Post>() {
            @Override
            public void onSuccess(Post result) {
                if (genreId > 0) {
                    postRepository.addBookGenre(result.getId(), genreId, token,
                            new PostRepository.Callback1<Void>() {
                        @Override public void onSuccess(Void v) { callback.onSuccess(result); }
                        @Override public void onError(String msg) { callback.onSuccess(result); }
                    });
                } else {
                    callback.onSuccess(result);
                }
            }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    public void updatePost(int postId, String title, String author, String dealType,
                           String description, String photoUrl, int genreId,
                           String token, PostRepository.Callback1<Void> callback) {
        String error = validatePost(title, author, dealType);
        if (error != null) { callback.onError(error); return; }

        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("author", author);
        updates.put("deal_type", dealType);
        updates.put("description", description);
        if (photoUrl != null && !photoUrl.isEmpty()) updates.put("photo_url", photoUrl);

        postRepository.updatePost(postId, updates, token, callback);
    }

    public void deletePost(int postId, String token, PostRepository.Callback1<Void> callback) {
        postRepository.deletePost(postId, token, callback);
    }

    public void getGenres(String token, GenresCallback callback) {
        postRepository.getGenres(token, new PostRepository.Callback1<List<Genre>>() {
            @Override public void onSuccess(List<Genre> result) { callback.onSuccess(result); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    public List<Post> filterPosts(List<Post> posts, String query, String city, String dealType, int genreId) {

        List<Post> result = new ArrayList<>();

        for (Post post : posts) {

            boolean matchQuery = true;
            boolean matchCity = true;
            boolean matchDeal = true;

            if (query != null && !query.trim().isEmpty()) {
                String q = query.toLowerCase();

                matchQuery =
                        (post.getTitle() != null && post.getTitle().toLowerCase().contains(q)) ||
                                (post.getAuthor() != null && post.getAuthor().toLowerCase().contains(q));
            }

            if (city != null && !city.isEmpty()) {
                matchCity =
                        post.getUser() != null &&
                                post.getUser().getCity() != null &&
                                post.getUser().getCity().equalsIgnoreCase(city);
            }
            if (dealType != null && !dealType.isEmpty()) {
                matchDeal =
                        post.getDealType() != null &&
                                post.getDealType().equalsIgnoreCase(dealType);
            }

            if (matchQuery && matchCity && matchDeal) {
                result.add(post);
            }
        }

        return result;
    }

    private String validatePost(String title, String author, String dealType) {
        if (title == null || title.trim().isEmpty()) return "Введіть назву книги";
        if (author == null || author.trim().isEmpty()) return "Введіть автора";
        if (dealType == null || dealType.isEmpty()) return "Оберіть тип угоди";
        return null;
    }
}
