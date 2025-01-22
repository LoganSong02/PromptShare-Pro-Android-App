package com.example.promptsharepro22.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.promptsharepro22.R;
import com.example.promptsharepro22.data.model.Post;
import com.example.promptsharepro22.utils.TimeUtil;
import com.example.promptsharepro22.viewmodel.PostViewModel;
import com.example.promptsharepro22.viewmodel.UserViewModel;

public class CreatePostFragment extends Fragment {
    private UserViewModel userViewModel;
    private PostViewModel postViewModel;

    private EditText titleEditText, descriptionEditText, authorNoteEditText;
    private Button createPostButton, kind1Button, kind2Button, kind3Button, kind4Button;

    private StringBuilder selectedKinds = new StringBuilder(); // Track selected LLM kinds

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        // Initialize UI components
        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        authorNoteEditText = view.findViewById(R.id.authorNoteText);
        createPostButton = view.findViewById(R.id.createPostButton);
        kind1Button = view.findViewById(R.id.kind1Button);
        kind2Button = view.findViewById(R.id.kind2Button);
        kind3Button = view.findViewById(R.id.kind3Button);
        kind4Button = view.findViewById(R.id.kind4Button);

        // Add all button listeners
        setupKindButton(kind1Button, getString(R.string.chatgpt_4o));
        setupKindButton(kind2Button, getString(R.string.claude));
        setupKindButton(kind3Button, getString(R.string.gemini));
        setupKindButton(kind4Button, getString(R.string.llama));

        // Initialize ViewModel
        userViewModel = UserViewModel.getInstance();
        postViewModel = PostViewModel.getInstance();

        // Set up the Create Post button click listener
        createPostButton.setOnClickListener(v -> createPost());

        return view;
    }

    private void createPost() {
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

        String currentUserId = userViewModel.getUserId().getValue();
        String createdAt = TimeUtil.getCurrentDate();
        Post post = new Post(title, description, currentUserId, authorNote, llmKind, createdAt, createdAt);

        // Save the post using the ViewModel
        postViewModel.createPost(post).observe(getViewLifecycleOwner(), postId -> {
            if (postId != null) {
                Toast.makeText(requireContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                // Optionally clear the fields after creation
                titleEditText.setText("");
                descriptionEditText.setText("");
                authorNoteEditText.setText("");
                selectedKinds.setLength(0); // Clear selected kinds
                resetButtonBackgrounds();
            } else {
                Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show();
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

            // Display a toast for the current selection
            // Toast.makeText(getContext(), "Selected: " + selectedKinds.toString(), Toast.LENGTH_SHORT).show();
        });
    }

    private void resetButtonBackgrounds() {
        getView().findViewById(R.id.kind1Button).setBackgroundTintList(getResources().getColorStateList(R.color.light_gray, null));
        getView().findViewById(R.id.kind2Button).setBackgroundTintList(getResources().getColorStateList(R.color.light_gray, null));
        getView().findViewById(R.id.kind3Button).setBackgroundTintList(getResources().getColorStateList(R.color.light_gray, null));
        getView().findViewById(R.id.kind4Button).setBackgroundTintList(getResources().getColorStateList(R.color.light_gray, null));
    }

    public String getSelectedKind() {
        return selectedKinds.toString();
    }
}
