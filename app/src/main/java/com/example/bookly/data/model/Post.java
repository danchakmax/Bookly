package com.example.bookly.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Post {

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("author")
    private String author;

    @SerializedName("deal_type")
    private String dealType;

    @SerializedName("description")
    private String description;

    @SerializedName("photo_url")
    private String photoUrl;

    @SerializedName("user")
    private User user;

    @SerializedName("genres")
    private List<Genre> genres;

    public Post() {}

    public Post(int userId, String title, String author, String dealType,
                String description, String photoUrl) {
        this.userId = userId;
        this.title = title;
        this.author = author;
        this.dealType = dealType;
        this.description = description;
        this.photoUrl = photoUrl;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDealType() { return dealType; }
    public void setDealType(String dealType) { this.dealType = dealType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Genre> getGenres() { return genres; }
    public void setGenres(List<Genre> genres) { this.genres = genres; }

    public String getDealTypeUkrainian() {
        if ("exchange".equals(dealType)) return "Обмін";
        if ("donation".equals(dealType)) return "Безкоштовно";
        return dealType;
    }
}