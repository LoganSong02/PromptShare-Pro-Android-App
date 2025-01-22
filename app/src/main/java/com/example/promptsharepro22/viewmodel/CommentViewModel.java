package com.example.promptsharepro22.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.promptsharepro22.data.model.Comment;
import com.example.promptsharepro22.data.repository.CommentDataAccess;

import java.util.List;

public class CommentViewModel {
    private static CommentViewModel instance;
    private final CommentDataAccess commentDataAccess;

    public CommentViewModel() {
        commentDataAccess = new CommentDataAccess();
    }

    public static synchronized CommentViewModel getInstance() {
        if (instance == null) {
            instance = new CommentViewModel();
        }
        return instance;
    }

    public LiveData<String> createComment(Comment comment) {
        return commentDataAccess.createComment(comment);
    }

    public LiveData<Comment> getComment(String id) {
        return commentDataAccess.getComment(id);
    }

    // Retrieve all comments for a specific post
    public LiveData<List<Comment>> getCommentsByPostId(String postId) {
        return commentDataAccess.getCommentsByPostId(postId);
    }

    // Retrieve all comments by a specific user
    public LiveData<List<Comment>> getCommentsByUserId(String userId) {
        return commentDataAccess.getCommentsByUserId(userId);
    }

    public LiveData<Boolean> updateComment(String id, Comment comment) {
        return commentDataAccess.updateComment(id, comment);
    }

    public LiveData<Boolean> deleteComment(String id) {
        return commentDataAccess.deleteComment(id);
    }

    public LiveData<Boolean> deleteCommentsByUser(String userId) {
        return commentDataAccess.deleteCommentsByUser(userId);
    }

    public LiveData<Boolean> deleteCommentsByPost(String postId) {
        return commentDataAccess.deleteCommentsByPost(postId);
    }
}
