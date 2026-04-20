package com.example.bookly.data.model;

import com.google.gson.annotations.SerializedName;

public class BooksGenre {
    @SerializedName("post_id")
    private int postId;

    @SerializedName("genre_id")
    private int genreId;

    public BooksGenre() {}
    public BooksGenre(int postId, int genreId) {
        this.postId = postId;
        this.genreId = genreId;
    }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    public int getGenreId() { return genreId; }
    public void setGenreId(int genreId) { this.genreId = genreId; }
}
