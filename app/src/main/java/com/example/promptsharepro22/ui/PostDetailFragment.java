package com.example.promptsharepro22.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.promptsharepro22.R;
import com.example.promptsharepro22.data.model.Comment;
import com.example.promptsharepro22.utils.TimeUtil;
import com.example.promptsharepro22.viewmodel.CommentViewModel;
import com.example.promptsharepro22.viewmodel.PostViewModel;
import com.example.promptsharepro22.viewmodel.UserViewModel;

public class PostDetailFragment extends Fragment implements CommentAdapter.OnCommentClickListener {

    private PostViewModel postViewModel;
    private CommentViewModel commentViewModel;
    private UserViewModel userViewModel;

    private TextView titleTextView, dateTextView, descriptionTextView, authorNoteTextView, llmKindTextView;
    private EditText commentEditText;
    private Button commentButton, submitCommentButton, cancelCommentButton;
    private ImageView backButton;
    private RatingBar ratingBar;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postViewModel = PostViewModel.getInstance();
        commentViewModel = CommentViewModel.getInstance();
        userViewModel = UserViewModel.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        // Initialize TextViews
        titleTextView = view.findViewById(R.id.postTitleTextView);
        dateTextView = view.findViewById(R.id.postDateTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        authorNoteTextView = view.findViewById(R.id.authorNoteTextView);
        llmKindTextView = view.findViewById(R.id.llmKindTextView);

        // Initialize comment-related views
        commentEditText = view.findViewById(R.id.commentEditText);
        commentButton = view.findViewById(R.id.commentButton);
        submitCommentButton = view.findViewById(R.id.submitCommentButton);
        cancelCommentButton = view.findViewById(R.id.cancelCommentButton);
        ratingBar = view.findViewById(R.id.ratingBar); // Initialize RatingBar

        backButton = view.findViewById(R.id.backButton);

        // Set up RecyclerView for comments
        recyclerViewComments = view.findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentAdapter = new CommentAdapter(null, this); // Pass 'null' for click listener
        recyclerViewComments.setAdapter(commentAdapter);

        String postId = getArguments() != null ? getArguments().getString("postId") : null;

        if (postId != null) {
            fetchPostDetails(postId);
            fetchComments(postId);

            commentButton.setOnClickListener(v -> {
                commentEditText.setVisibility(View.VISIBLE);
                submitCommentButton.setVisibility(View.VISIBLE);
                cancelCommentButton.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                commentButton.setVisibility(View.GONE); // Hide "Add Comment" button
            });

            submitCommentButton.setOnClickListener(v -> addComment(postId));
            cancelCommentButton.setOnClickListener(v -> resetCommentInput());
            backButton.setOnClickListener(v -> goBack());
        } else {
            Toast.makeText(requireContext(), "Post ID is missing", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchPostDetails(String postId) {
        postViewModel.getPost(postId).observe(getViewLifecycleOwner(), post -> {
            if (post != null) {
                titleTextView.setText(post.getTitle());
                dateTextView.setText(post.getCreatedAt().split("T")[0]);
                descriptionTextView.setText(post.getContent());
                authorNoteTextView.setText(post.getAuthorNote().isEmpty() ? "N/A" : post.getAuthorNote());
                llmKindTextView.setText(post.getLLMKind());
            } else {
                Toast.makeText(requireContext(), "Post not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchComments(String postId) {
        commentViewModel.getCommentsByPostId(postId).observe(getViewLifecycleOwner(), comments -> {
            if (comments != null) {
                for (Comment comment : comments) {
                    String userId = comment.getUserId();
                    userViewModel.getUser(userId).observe(getViewLifecycleOwner(), user -> {
                        commentAdapter.setComments(comments);
                    });
                }
            }
        });
    }

    private void addComment(String postId) {
        String commentContent = commentEditText.getText().toString().trim();
        float rating = ratingBar.getRating();

        if (!commentContent.isEmpty()) {
            userViewModel.getUserId().observe(getViewLifecycleOwner(), userId -> {
                if (userId != null) {
                    String createdAt = TimeUtil.getCurrentDate();
                    Comment comment = new Comment(commentContent, postId, userId, createdAt, createdAt, rating);

                    commentViewModel.createComment(comment).observe(getViewLifecycleOwner(), commentId -> {
                        if (commentId != null) {
                            Toast.makeText(requireContext(), "Comment added", Toast.LENGTH_SHORT).show();
                            resetCommentInput();
                            fetchComments(postId); // Refresh comments after adding
                        } else {
                            Toast.makeText(requireContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to reset comment input and show "Add Comment" button
    private void resetCommentInput() {
        commentEditText.setText("");
        ratingBar.setRating(0);
        commentEditText.setVisibility(View.GONE);
        submitCommentButton.setVisibility(View.GONE);
        cancelCommentButton.setVisibility(View.GONE);
        ratingBar.setVisibility(View.GONE);
        commentButton.setVisibility(View.VISIBLE); // Show "Add Comment" button
    }

    @Override
    public void onCommentClick(Comment comment) {
        // Handle comment click event only if it is your own comment
        String myUserId = userViewModel.getUserId().getValue();
        if (myUserId != null && myUserId.equals(comment.getUserId())) {
            // Open EditCommentFragment with comment ID
            Bundle bundle = new Bundle();
            bundle.putString("commentId", comment.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, EditCommentFragment.class, bundle)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void goBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
