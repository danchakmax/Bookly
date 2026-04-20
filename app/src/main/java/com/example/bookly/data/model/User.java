package com.example.bookly.data.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("password")
    private String password;

    @SerializedName("about")
    private String about;

    @SerializedName("role")
    private String role;

    @SerializedName("region")
    private String region;

    @SerializedName("district")
    private String district;

    @SerializedName("city")
    private String city;

    @SerializedName("photo_url")
    private String photoUrl;

    public User() {}

    public User(String name, String email, String phone, String password,
                String region, String district, String city) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.region = region;
        this.district = district;
        this.city = city;
        this.role = "user";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
