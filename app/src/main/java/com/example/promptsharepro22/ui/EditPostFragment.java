package com.example.promptsharepro22.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.promptsharepro22.R;
import com.example.promptsharepro22.data.model.Post;
import com.example.promptsharepro22.utils.TimeUtil;
import com.example.promptsharepro22.viewmodel.CommentViewModel;
import com.example.promptsharepro22.viewmodel.PostViewModel;

public class EditPostFragment extends Fragment {
    private PostViewModel postViewModel;
    private CommentViewModel commentViewModel;

    private EditText titleEditText, descriptionEditText, authorNoteEditText;
    private Button kind1Button, kind2Button, kind3Button, kind4Button, saveButton, deleteButton;
    private ImageView backButton;
    private String postId;

    private StringBuilder selectedKinds = new StringBuilder(); // Track selected LLM kinds

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);

        // Initialize UI components
        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        authorNoteEditText = view.findViewById(R.id.authorNoteText);
        kind1Button = view.findViewById(R.id.kind1Button);
        kind2Button = view.findViewById(R.id.kind2Button);
        kind3Button = view.findViewById(R.id.kind3Button);
        kind4Button = view.findViewById(R.id.kind4Button);
        backButton = view.findViewById(R.id.backButton);
        saveButton = view.findViewById(R.id.editPostButton);
        deleteButton = view.findViewById(R.id.deletePostButton);

        // Initialize ViewModel
        postViewModel = PostViewModel.getInstance();
        commentViewModel = CommentViewModel.getInstance();

        // Check if a postId is passed as an argument
        if (getArguments() != null && getArguments().containsKey("postId")) {
            postId = getArguments().getString("postId");
            loadPostData(postId);
        }

        // Add listeners to LLM buttons
        setupKindButton(kind1Button, getString(R.string.chatgpt_4o));
        setupKindButton(kind2Button, getString(R.string.claude));
        setupKindButton(kind3Button, getString(R.string.gemini));
        setupKindButton(kind4Button, getString(R.string.llama));

        // Set up the Update button listener
        // Set click listeners
        backButton.setOnClickListener(v -> goBack());
        saveButton.setOnClickListener(v -> {
            if (postId != null) {
                updatePost();
            }
        });
        deleteButton.setOnClickListener(v -> {
            if (postId != null) {
                confirmDeletePost();
            }
        });

        return view;
    }

    private void loadPostData(String postId) {
        // Fetch the post data and populate fields
        postViewModel.getPost(postId).observe(getViewLifecycleOwner(), post -> {
            if (post != null) {
                titleEditText.setText(post.getTitle());
                descriptionEditText.setText(post.getContent());
                authorNoteEditText.setText(post.getAuthorNote());

                // Load LLM kinds and set selected button backgrounds
                selectedKinds = new StringBuilder(post.getLLMKind());
                updateButtonBackgrounds();
            }
        });
    }

    private void updatePost() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String authorNote = authorNoteEditText.getText().toString().trim();
        String llmKind = selectedKinds.toString();
        while (llmKind.startsWith(",")) {
            llmKind = llmKind.substring(1).trim(); // Remove leading comma
        }

        if (title.isEmpty() || description.isEmpty() || llmKind.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String updatedAt = TimeUtil.getCurrentDate();
        Post updatedPost = new Post();
        updatedPost.setTitle(title);
        updatedPost.setContent(description);
        updatedPost.setAuthorNote(authorNote);
        updatedPost.setLLMKind(llmKind);
        updatedPost.setUpdatedAt(updatedAt);

        postViewModel.updatePost(postId, updatedPost).observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Post updated successfully", Toast.LENGTH_SHORT).show();
                goBack();
            } else {
                Toast.makeText(requireContext(), "Failed to update post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeletePost() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deletePost())  // Proceed to delete if confirmed
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())  // Dismiss dialog if canceled
                .show();
    }

    private void deletePost() {
        postViewModel.deletePost(postId).observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Post and related comments deleted successfully", Toast.LENGTH_SHORT).show();
                goBack();
            } else {
                Toast.makeText(requireContext(), "Failed to delete post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupKindButton(Button button, String kind) {
        button.setOnClickListener(v -> {
            // Check if the kind is already selected
            if (selectedKinds.toString().contains(kind)) {
                // If already selected, deselect it
                int startIndex = selectedKinds.indexOf(kind);
                int endIndex = startIndex + kind.length();
                selectedKinds.delete(startIndex, endIndex);

                // Update button background to indicate deselection
                button.setBackgroundTintList(getResources().getColorStateList(R.color.light_gray, null));
            } else {
                // If not selected, add it to selectedKinds
                if (selectedKinds.length() > 0) {
                    selectedKinds.append(", ");
                }
                selectedKinds.append(kind);

                // Update button background to indicate selection
                button.setBackgroundTintList(getResources().getColorStateList(R.color.light_purple, null));
            }
        });
    }

    private void updateButtonBackgrounds() {
        // Reset all button backgrounds to deselected state
        resetButtonBackgrounds();

        // Update button backgrounds based on selected kinds
        if (selectedKinds.toString().contains(getString(R.string.chatgpt_4o))) {
            kind1Button.setBackgroundTintList(getResources().getColorStateList(R.color.light_purple, null));
        }
        if (selectedKinds.toString().contains(getString(R.string.claude))) {
            kind2Button.setBackgroundTintList(getResources().getColorStateList(R.color.light_purple, null));
        }
        if (selectedKinds.toString().contains(getString(R.string.gemini))) {
            kind3Button.setBackgroundTintList(getResources().getColorStateList(R.color.light_purple, null));
        }
        if (selectedKinds.toString().contains(getString(R.string.llama))) {
            kind4Button.setBackgroundTintList(getResources().getColorStateList(R.color.light_purple, null));
        }
    }

    private void resetButtonBackgrounds() {
        kind1Button.setBackgroundTintList(getResources().getColorStateList(R.color.light_gray, null));
        kind2Button.setBackgroundTintList(getResources().getColorStateList(R.color.light_gray, null));
        kind3Button.setBackgroundTintList(getResources().getColorStateList(R.color.light_gray, null));
        kind4Button.setBackgroundTintList(getResources().getColorStateList(R.color.light_gray, null));
    }

    private void goBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
