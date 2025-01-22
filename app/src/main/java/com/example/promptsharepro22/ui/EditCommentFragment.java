package com.example.promptsharepro22.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.promptsharepro22.R;
import com.example.promptsharepro22.data.model.Comment;
import com.example.promptsharepro22.utils.TimeUtil;
import com.example.promptsharepro22.viewmodel.CommentViewModel;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EditCommentFragment extends Fragment {

    private EditText editCommentEditText;
    private Button updateCommentButton, deleteCommentButton;
    private ImageView backButton;
    private RatingBar ratingBar;
    private CommentViewModel commentViewModel;
    private String commentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commentViewModel = CommentViewModel.getInstance();

        if (getArguments() != null) {
            commentId = getArguments().getString("commentId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_comment, container, false);

        editCommentEditText = view.findViewById(R.id.editCommentEditText);
        updateCommentButton = view.findViewById(R.id.updateCommentButton);
        deleteCommentButton = view.findViewById(R.id.deleteCommentButton);
        backButton = view.findViewById(R.id.backButton);
        ratingBar = view.findViewById(R.id.editCommentRatingBar);

        loadCommentData();

        updateCommentButton.setOnClickListener(v -> updateComment());
        deleteCommentButton.setOnClickListener(v -> confirmDeleteComment());
        backButton.setOnClickListener(v -> goBack());

        return view;
    }

    private void loadCommentData() {
        commentViewModel.getComment(commentId).observe(getViewLifecycleOwner(), comment -> {
            if (comment != null) {
                editCommentEditText.setText(comment.getContent());
                ratingBar.setRating(comment.getRating());
            }
        });
    }

    private void updateComment() {
        String updatedContent = editCommentEditText.getText().toString().trim();
        float updatedRating = ratingBar.getRating();

        if (updatedContent.isEmpty()) {
            Toast.makeText(getContext(), "Please enter some text", Toast.LENGTH_SHORT).show();
            return;
        }

        Comment updatedComment = new Comment();
        String updatedAt = TimeUtil.getCurrentDate();
        updatedComment.setContent(updatedContent);
        updatedComment.setRating(updatedRating);
        updatedComment.setUpdatedAt(updatedAt);

        commentViewModel.updateComment(commentId, updatedComment).observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Comment updated successfully", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Failed to update comment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteComment() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteComment())  // Proceed to delete if confirmed
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())  // Dismiss dialog if canceled
                .show();
    }

    private void deleteComment() {
        commentViewModel.deleteComment(commentId).observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Comment deleted successfully", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Failed to delete comment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
