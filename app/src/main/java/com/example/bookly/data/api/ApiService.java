package com.example.bookly.data.api;

import com.example.bookly.data.model.BooksGenre;
import com.example.bookly.data.model.Complaint;
import com.example.bookly.data.model.Genre;
import com.example.bookly.data.model.Post;
import com.example.bookly.data.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ───────── AUTH (🔥 ДОДАНО) ─────────

    @POST("auth/v1/signup")
    Call<Map<String, Object>> signUp(
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType,
            @Body Map<String, Object> body
    );

    // ───────── USERS ─────────

    @GET("rest/v1/users")
    Call<List<User>> getUsers(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("email") String emailEq,
            @Query("select") String select
    );

    @GET("rest/v1/users")
    Call<List<User>> getUserById(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String idEq,
            @Query("select") String select
    );

    @POST("rest/v1/users")
    Call<List<User>> createUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Header("Prefer") String prefer,
            @Body User user
    );

    @PATCH("rest/v1/users")
    Call<List<User>> updateUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Header("Prefer") String prefer,
            @Query("id") String idEq,
            @Body Map<String, Object> updates
    );

    @DELETE("rest/v1/users")
    Call<Void> deleteUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String idEq
    );

    // ───────── POSTS ─────────

    @GET("rest/v1/posts")
    Call<List<Post>> getPosts(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("select") String select,
            @Query("order") String order
    );

    @GET("rest/v1/posts")
    Call<List<Post>> getPostsByUser(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("user_id") String userIdEq,
            @Query("select") String select
    );

    @GET("rest/v1/posts")
    Call<List<Post>> getPostById(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String idEq,
            @Query("select") String select
    );

    @POST("rest/v1/posts")
    Call<List<Post>> createPost(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Header("Prefer") String prefer,
            @Body Post post
    );

    @PATCH("rest/v1/posts")
    Call<List<Post>> updatePost(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Header("Prefer") String prefer,
            @Query("id") String idEq,
            @Body Map<String, Object> updates
    );

    @DELETE("rest/v1/posts")
    Call<Void> deletePost(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String idEq
    );

    // ───────── GENRES ─────────

    @GET("rest/v1/genres")
    Call<List<Genre>> getGenres(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token
    );

    // ───────── BOOKS_GENRES ─────────

    @POST("rest/v1/books_genres")
    Call<Void> addBookGenre(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Body BooksGenre booksGenre
    );

    @DELETE("rest/v1/books_genres")
    Call<Void> deleteBookGenres(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("post_id") String postIdEq
    );

    // ───────── COMPLAINTS ─────────

    @GET("rest/v1/complaints")
    Call<List<Complaint>> getComplaints(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("select") String select
    );

    @POST("rest/v1/complaints")
    Call<List<Complaint>> createComplaint(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Header("Prefer") String prefer,
            @Body Complaint complaint
    );

    @DELETE("rest/v1/complaints")
    Call<Void> deleteComplaint(
            @Header("apikey") String apiKey,
            @Header("Authorization") String token,
            @Query("id") String idEq
    );
}