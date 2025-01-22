package com.example.promptsharepro22.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.promptsharepro22.R;
import com.example.promptsharepro22.data.model.Comment;
import com.example.promptsharepro22.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments = new ArrayList<>();
    private UserViewModel userViewModel = UserViewModel.getInstance();
    private OnCommentClickListener listener;

    public CommentAdapter(List<Comment> comments, OnCommentClickListener listener) {
        this.comments = comments != null ? comments : new ArrayList<>();
        this.listener = listener;
    }

    public interface OnCommentClickListener {
        void onCommentClick(Comment comment);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.commentContent.setText(comment.getContent());

        String myUserId = userViewModel.getUserId().getValue();
        String commentUserId = comment.getUserId();

        if (myUserId != null && myUserId.equals(commentUserId)) {
            holder.commentUser.setText("You");
        } else {
            userViewModel.getUserEmail(commentUserId).observeForever(email -> {
                holder.commentUser.setText(email);
            });
        }

        if (comment.getCreatedAt() != null && !comment.getCreatedAt().isEmpty()) {
            String dateOnly = comment.getCreatedAt().split("T")[0]; // Extract date part before "T"
            holder.commentDate.setText(dateOnly);
        } else {
            holder.commentDate.setText("N/A");
        }

        holder.ratingBar.setRating(comment.getRating());

        // Set click listener to open edit/delete options
        holder.itemView.setOnClickListener(v -> listener.onCommentClick(comment));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentContent, commentUser, commentDate;
        RatingBar ratingBar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentContent = itemView.findViewById(R.id.commentContent);
            commentUser = itemView.findViewById(R.id.commentUser);
            commentDate = itemView.findViewById(R.id.commentDate);
            ratingBar = itemView.findViewById(R.id.commentRatingBar);
        }
    }
}
