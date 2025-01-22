package com.example.promptsharepro22.viewmodel;

import androidx.lifecycle.LiveData;

import com.example.promptsharepro22.data.model.Post;
import com.example.promptsharepro22.data.repository.PostDataAccess;

import java.util.List;

public class PostViewModel {
    private static PostViewModel instance;
    private final PostDataAccess postDataAccess;

    public PostViewModel() {
        postDataAccess = new PostDataAccess();
    }

    public static synchronized PostViewModel getInstance() {
        if (instance == null) {
            instance = new PostViewModel();
        }
        return instance;
    }

    // Create a post
    public LiveData<String> createPost(Post post) {
        return postDataAccess.createPost(post); // Returns the new post ID
    }

    // Get a post by ID
    public LiveData<Post> getPost(String postId) {
        return postDataAccess.getPost(postId);
    }

    // Get all posts by a specific user
    public LiveData<List<Post>> getPostsByUser(String userId) {
        return postDataAccess.getPostsByUserId(userId);
    }

    // Update an existing post
    public LiveData<Boolean> updatePost(String postId, Post post) {
        return postDataAccess.updatePost(postId, post);
    }

    // Delete a post
    public LiveData<Boolean> deletePost(String postId) {
        return postDataAccess.deletePost(postId);
    }

    public LiveData<Boolean> deletePostsByUser(String userId) {
        return postDataAccess.deletePostsByUser(userId);
    }

    // Fetch all posts (this could be updated for a paginated approach)
    public LiveData<List<Post>> getAllPosts() {
        return postDataAccess.getAllPosts();
    }

    // Search posts by query
    public LiveData<List<Post>> searchPosts(String query, String searchType) {
        return postDataAccess.searchPosts(query, searchType);
    }

    // PostViewModel.java
    public LiveData<List<Post>> getPostsSortedBy(String sortBy) {
        return postDataAccess.getPostsSortedBy(sortBy);
    }
}
