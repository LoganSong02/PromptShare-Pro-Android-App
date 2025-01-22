package com.example.promptsharepro22.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.promptsharepro22.R;
import com.example.promptsharepro22.data.model.Post;
import com.example.promptsharepro22.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;
    private UserViewModel userViewModel = UserViewModel.getInstance();
    private OnPostClickListener postClickListener; // Click listener interface

    public interface OnPostClickListener {
        void onPostClick(String postId); // Method to be implemented
    }

    public PostAdapter(List<Post> posts, OnPostClickListener listener) {
        this.posts = posts != null ? posts : new ArrayList<>();  // Initialize with an empty list if null
        this.postClickListener = listener; // Set the click listener
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.postTitle.setText(post.getTitle());

        String myUserId = userViewModel.getUserId().getValue();
        String postUserId = post.getUserId();

        if (myUserId != null && myUserId.equals(postUserId)) {
            holder.postAuthorEmail.setText("You");
        } else {
            userViewModel.getUserEmail(postUserId).observeForever(email -> {
                holder.postAuthorEmail.setText(email);
            });
        }

        if (post.getCreatedAt() != null && !post.getCreatedAt().isEmpty()) {
            String fullDate = post.getCreatedAt();
            String dateOnly = fullDate.split("T")[0];  // Extract the date portion before "T"
            holder.postDate.setText(dateOnly);
        } else {
            holder.postDate.setText("N/A");
        }

        // Set click listener on the item view
        holder.itemView.setOnClickListener(v -> {
            if (postClickListener != null) {
                postClickListener.onPostClick(post.getId()); // Notify listener with post ID
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts != null ? posts.size() : 0;  // Return 0 if posts list is null
    }

    public void setPosts(List<Post> newPosts) {
        Log.d("PostAdapter", "Setting posts. Count: " + (newPosts != null ? newPosts.size() : 0));
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postDate, postAuthorEmail;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postDate = itemView.findViewById(R.id.postDate);
            postAuthorEmail = itemView.findViewById(R.id.postAuthorEmail);
        }
    }
}
