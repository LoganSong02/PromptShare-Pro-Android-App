package com.example.promptsharepro22.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.promptsharepro22.data.model.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentDataAccess {
    private final DatabaseReference databaseRef;

    public CommentDataAccess() {
        databaseRef = FirebaseDatabase.getInstance().getReference("comments");
    }

    public LiveData<String> createComment(Comment comment) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        DatabaseReference newCommentRef = databaseRef.push();
        comment.setId(newCommentRef.getKey());
        newCommentRef.setValue(comment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(comment.getId());
            } else {
                liveData.setValue(null); // Indicate failure
            }
        });
        return liveData;
    }

    public LiveData<Comment> getComment(String id) {
        MutableLiveData<Comment> liveData = new MutableLiveData<>();
        databaseRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Comment comment = snapshot.getValue(Comment.class);
                liveData.setValue(comment);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                liveData.setValue(null); // Handle error if needed
            }
        });
        return liveData;
    }

    // Get all comments related to a specific post
    public LiveData<List<Comment>> getCommentsByPostId(String postId) {
        MutableLiveData<List<Comment>> liveData = new MutableLiveData<>();
        Query query = databaseRef.orderByChild("postId").equalTo(postId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Comment> commentList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Comment comment = child.getValue(Comment.class);
                    commentList.add(comment);
                }
                liveData.setValue(commentList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                liveData.setValue(null); // Handle error as needed
            }
        });
        return liveData;
    }

    // Get all comments made by a specific user
    public LiveData<List<Comment>> getCommentsByUserId(String userId) {
        MutableLiveData<List<Comment>> liveData = new MutableLiveData<>();
        Query query = databaseRef.orderByChild("userId").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Comment> commentList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Comment comment = child.getValue(Comment.class);
                    commentList.add(comment);
                }
                liveData.setValue(commentList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                liveData.setValue(null); // Handle error as needed
            }
        });
        return liveData;
    }

    public LiveData<Boolean> updateComment(String id, Comment comment) {
        Map<String, Object> update = comment.buildUpdateMap();
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        databaseRef.child(id).updateChildren(update).addOnCompleteListener(task -> {
            liveData.setValue(task.isSuccessful());
        });
        return liveData;
    }

    public LiveData<Boolean> deleteComment(String id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        databaseRef.child(id).removeValue().addOnCompleteListener(task -> {
            liveData.setValue(task.isSuccessful());
        });
        return liveData;
    }

    public LiveData<Boolean> deleteCommentsByUser(String userId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        DatabaseReference commentsRef = databaseRef.child("comments");

        commentsRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                        commentSnapshot.getRef().removeValue();
                    }
                    result.setValue(true);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    result.setValue(false); // Deletion failed
                }
            });
        return result;
    }

    public LiveData<Boolean> deleteCommentsByPost(String postId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        DatabaseReference commentsRef = databaseRef.child("comments");

        commentsRef.orderByChild("postId").equalTo(postId)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                        commentSnapshot.getRef().removeValue();
                    }
                    result.setValue(true);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    result.setValue(false); // Deletion failed
                }
            });
        return result;
    }

}
