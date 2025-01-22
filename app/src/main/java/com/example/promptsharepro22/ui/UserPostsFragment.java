package com.example.promptsharepro22.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.promptsharepro22.R;
import com.example.promptsharepro22.data.model.Post;
import com.example.promptsharepro22.viewmodel.PostViewModel;
import com.example.promptsharepro22.viewmodel.UserViewModel;

import java.util.List;

public class UserPostsFragment extends Fragment implements PostAdapter.OnPostClickListener {

    private UserViewModel userViewModel;
    private PostViewModel postViewModel;
    private PostAdapter postAdapter;
    private RecyclerView userPostsRecyclerView;
    private ImageView backButton;
    private static final String TAG = "UserPostsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_posts, container, false);

        userPostsRecyclerView = view.findViewById(R.id.userPostsRecyclerView);
        backButton = view.findViewById(R.id.backButton);

        // Initialize the PostAdapter and set it to the RecyclerView
        postAdapter = new PostAdapter(null, this); // Pass 'this' for click listener
        userPostsRecyclerView.setAdapter(postAdapter);
        userPostsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize the ViewModel
        userViewModel = UserViewModel.getInstance();
        postViewModel = PostViewModel.getInstance();

        // Get the currently logged-in user ID and load their posts
        String currentUserId = userViewModel.getUserId().getValue();
        if (currentUserId != null) {
            loadUserPosts(currentUserId);
        } else {
            Log.e(TAG, "User ID is null. Unable to load user-specific posts.");
        }

        // Set click listeners
        backButton.setOnClickListener(v -> backToProfile());

        return view;
    }

    private void loadUserPosts(String userId) {
        // Observe posts by the specific user
        postViewModel.getPostsByUser(userId).observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if (posts != null && !posts.isEmpty()) {
                    Log.d(TAG, "User posts loaded: " + posts.size());
                    postAdapter.setPosts(posts);
                } else {
                    Log.d(TAG, "No posts found for the user.");
                    postAdapter.setPosts(posts); // Setting an empty list to clear the adapter if no posts
                }
            }
        });
    }

    @Override
    public void onPostClick(String postId) {
        // Create and load the EditPostFragment with the selected post ID
        EditPostFragment editPostFragment = new EditPostFragment();
        Bundle bundle = new Bundle();
        bundle.putString("postId", postId);
        editPostFragment.setArguments(bundle);

        // Replace the current fragment with EditPostFragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, editPostFragment)
                .addToBackStack(null)
                .commit();
    }

    private void backToProfile() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
