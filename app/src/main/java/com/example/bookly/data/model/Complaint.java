package com.example.bookly.data.model;

import com.google.gson.annotations.SerializedName;

public class Complaint {
    @SerializedName("id")
    private int id;

    @SerializedName("text")
    private String text;

    @SerializedName("date")
    private String date;

    @SerializedName("post_id")
    private int postId;

    @SerializedName("complainant_id")
    private int complainantId;

    private Post post;
    private User complainant;

    public Complaint() {}

    public Complaint(String text, int postId, int complainantId) {
        this.text = text;
        this.postId = postId;
        this.complainantId = complainantId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }
    public int getComplainantId() { return complainantId; }
    public void setComplainantId(int complainantId) { this.complainantId = complainantId; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public User getComplainant() { return complainant; }
    public void setComplainant(User complainant) { this.complainant = complainant; }
}
