package com.example.promptsharepro22.data.model;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private String id;
    private String title;
    private String content;
    private String authorNote;
    private String llmkind;
    private String userId;
    private String createdAt;
    private String updatedAt;

    // Empty constructor for Firebase
    public Post() {}

    // Constructor with parameters
    public Post(String title, String content, String userId, String authorNote,
                String LLMKind, String createdAt, String updatedAt) {
        this.id = null; // Set by Firebase
        this.title = title;
        this.content = content;
        this.authorNote = authorNote;
        this.llmkind = LLMKind;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorNote() {
        return authorNote;
    }

    public String getLLMKind() {
        return llmkind;
    }

    public String getUserId() {
        return userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthorNote(String authorNote) {
        this.authorNote = authorNote;
    }

    public void setLLMKind(String LLMKind) {
        this.llmkind = LLMKind;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Method to create a Map for partial updates
    public Map<String, Object> buildUpdateMap() {
        Map<String, Object> updateMap = new HashMap<>();

        if (title != null) updateMap.put("title", title);
        if (content != null) updateMap.put("content", content);
        if (authorNote != null) updateMap.put("authorNote", authorNote);
        if (llmkind != null) updateMap.put("llmkind", llmkind);
        if (userId != null) updateMap.put("userId", userId);
        if (createdAt != null) updateMap.put("createdAt", createdAt);
        if (updatedAt != null) updateMap.put("updatedAt", updatedAt);

        return updateMap;
    }

}
