package com.example.promptsharepro22.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.promptsharepro22.R;
import com.example.promptsharepro22.utils.AvaterUtil;
import com.example.promptsharepro22.viewmodel.CommentViewModel;
import com.example.promptsharepro22.viewmodel.PostViewModel;
import com.example.promptsharepro22.viewmodel.UserViewModel;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private TextView usernameTextView, emailTextView;
    private Button editButton, logoutButton, deleteButton, viewAllPostsButton, viewAllCommentsButton;

    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        profileImageView = view.findViewById(R.id.profilePicture);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
//        postCountTextView = view.findViewById(R.id.postCountTextView);
//        commentCountTextView = view.findViewById(R.id.commentCountTextView);
        editButton = view.findViewById(R.id.editButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        deleteButton = view.findViewById(R.id.deleteAccountButton);
        viewAllPostsButton = view.findViewById(R.id.viewAllPostsButton);
        viewAllCommentsButton = view.findViewById(R.id.viewAllCommentsButton);

        // Initialize ViewModel
        userViewModel = UserViewModel.getInstance();

        // Load profile data
        loadProfileData();

        // Set up button listeners
        editButton.setOnClickListener(v -> editProfile());
        logoutButton.setOnClickListener(v -> logout());
        deleteButton.setOnClickListener(v -> confirmDeleteAccount());
        viewAllPostsButton.setOnClickListener(v -> viewAllPosts());
        viewAllCommentsButton.setOnClickListener(v -> viewAllComments());

        return view;
    }

    private void loadProfileData() {
        String currentUserId = userViewModel.getUserId().getValue();

        userViewModel.getUser(currentUserId).observe(requireActivity(), fetchedUser -> {
            if (fetchedUser != null) {
                usernameTextView.setText(fetchedUser.getUsername());
                emailTextView.setText(fetchedUser.getEmail());
//                postCountTextView.setText("1");
//                commentCountTextView.setText("1");

                // Load profile image
                Drawable profileImage = AvaterUtil.getInitialsDrawable(fetchedUser.getFirstName(), fetchedUser.getLastName());
                profileImageView.setImageDrawable(profileImage);
            } else {
                Toast.makeText(requireContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void editProfile() {
        // Create and load the EditProfileFragment
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, editProfileFragment)
                .addToBackStack(null)
                .commit();
    }

    private void logout() {
        userViewModel.logout();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account (Super Dangerous Action!!!)")
                .setMessage("Are you sure you want to delete this account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())  // Proceed to delete if confirmed
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())  // Dismiss dialog if canceled
                .show();
    }

    private void deleteAccount() {
        String userId = userViewModel.getUserId().getValue();

        userViewModel.deleteUser(userId).observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                startActivity(intent);
                requireActivity().finish();
            } else {
                Toast.makeText(requireContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void viewAllPosts() {
        // Create and load the UserPostsFragment
        UserPostsFragment userPostsFragment = new UserPostsFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, userPostsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void viewAllComments() {
        // Create and load the UserCommentFragment
        UserCommentFragment userCommentFragment = new UserCommentFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, userCommentFragment)
                .addToBackStack(null)
                .commit();
    }
}
