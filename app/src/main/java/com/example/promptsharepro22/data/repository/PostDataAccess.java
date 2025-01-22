package com.example.promptsharepro22.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.promptsharepro22.data.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostDataAccess {
    private final DatabaseReference databaseRef;

    public PostDataAccess() {
        databaseRef = FirebaseDatabase.getInstance().getReference("posts");
    }

    public LiveData<String> createPost(Post post) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        DatabaseReference newPostRef = databaseRef.push();
        post.setId(newPostRef.getKey());
        newPostRef.setValue(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(post.getId());
            } else {
                liveData.setValue(null); // Indicate failure
            }
        });
        return liveData;
    }

    public LiveData<Post> getPost(String id) {
        MutableLiveData<Post> liveData = new MutableLiveData<>();
        databaseRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                liveData.setValue(post);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                liveData.setValue(null); // Handle error if needed
            }
        });
        return liveData;
    }

    public LiveData<List<Post>> getPostsByUserId(String userId) {
        MutableLiveData<List<Post>> liveData = new MutableLiveData<>();
        databaseRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        posts.add(post);
                    }
                }
                liveData.setValue(posts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                liveData.setValue(null); // Or handle the error
            }
        });
        return liveData;
    }

    // Retrieve all posts
    public LiveData<List<Post>> getAllPosts() {
        MutableLiveData<List<Post>> liveData = new MutableLiveData<>();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Post> postList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Post post = child.getValue(Post.class);
                    postList.add(post);
                }
                liveData.setValue(postList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                liveData.setValue(null); // Handle error as needed
            }
        });
        return liveData;
    }

    public LiveData<Boolean> updatePost(String postId, Post post) {
        Map<String, Object> updates = post.buildUpdateMap();
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        databaseRef.child(postId).updateChildren(updates).addOnCompleteListener(task -> {
            liveData.setValue(task.isSuccessful());
        });
        return liveData;
    }

    public LiveData<Boolean> deletePost(String id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        databaseRef.child(id).removeValue().addOnCompleteListener(task -> {
            liveData.setValue(task.isSuccessful());
        });
        return liveData;
    }

    public LiveData<Boolean> deletePostsByUser(String userId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        databaseRef.child("posts").orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        postSnapshot.getRef().removeValue();
                    }
                    result.setValue(true);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    result.setValue(false);
                }
            });
        return result;
    }

    public LiveData<List<Post>> searchPosts(String query, String searchType) {
        MutableLiveData<List<Post>> liveData = new MutableLiveData<>();
        Log.d("PostDataAccess", "Searching posts with query: " + query + " and searchType: " + searchType);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Post> filteredPosts = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Post post = child.getValue(Post.class);
                    if (post != null) {
                        String llmKind = post.getLLMKind() != null ? post.getLLMKind().toLowerCase() : "";
                        String title = post.getTitle() != null ? post.getTitle().toLowerCase() : "";
                        String content = post.getContent() != null ? post.getContent().toLowerCase() : "";

                        boolean matches = false;

                        switch (searchType) {
                            case "Search by LLM Kind":
                                matches = llmKind.contains(query.toLowerCase());
                                break;
                            case "Search by Titles":
                                matches = title.contains(query.toLowerCase());
                                break;
                            case "Full Text Search":
                                matches = content.contains(query.toLowerCase());
                                break;
                            default:
                                matches = llmKind.contains(query.toLowerCase()) ||
                                        title.contains(query.toLowerCase()) ||
                                        content.contains(query.toLowerCase());
                                break;
                        }

                        if (matches) {
                            filteredPosts.add(post);
                        }
                    }
                }
                liveData.setValue(filteredPosts);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("PostDataAccess", "Database error: " + error.getMessage());
                liveData.setValue(null); // Handle error if needed
            }
        });
        return liveData;
    }

    // PostDataAccess.java
    public LiveData<List<Post>> getPostsSortedBy(String sortBy) {
        MutableLiveData<List<Post>> liveData = new MutableLiveData<>();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Post> postList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Post post = child.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }

                // Apply sorting based on criteria
                if (sortBy != null) {
                    switch (sortBy) {
                        case "createdAt":
                            postList.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
                            break;
                        case "title":
                            postList.sort((p1, p2) -> p1.getTitle().compareToIgnoreCase(p2.getTitle()));
                            break;
                        case "llmKind":
                            postList.sort((p1, p2) -> p1.getLLMKind().compareToIgnoreCase(p2.getLLMKind()));
                            break;
                        default: // Default sorting (e.g., by ID)
                            postList.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
                            break;
                    }
                }

                liveData.setValue(postList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                liveData.setValue(null); // Handle errors as needed
            }
        });
        return liveData;
    }
}
