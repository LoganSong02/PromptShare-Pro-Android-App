package com.example.promptsharepro22.data.model;

import java.util.HashMap;
import java.util.Map;

public class Comment {
    private String id;
    private String content;
    private String postId;
    private String userId;
    private String createdAt;
    private String updatedAt;
    private float rating; // New field for rating

    // Empty constructor for Firebase
    public Comment() {}

    // Constructor with parameters, including the rating
    public Comment(String content, String postId, String userId, String createdAt, String updatedAt, float rating) {
        this.id = null;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.rating = rating;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    // Method to create a Map for partial updates
    public Map<String, Object> buildUpdateMap() {
        Map<String, Object> updateMap = new HashMap<>();

        if (content != null) updateMap.put("content", content);
        if (updatedAt != null) updateMap.put("updatedAt", updatedAt);
        updateMap.put("rating", rating);

        return updateMap;
    }
}
