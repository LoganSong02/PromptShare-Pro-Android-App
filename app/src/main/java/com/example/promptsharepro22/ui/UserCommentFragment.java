package com.example.promptsharepro22.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.promptsharepro22.R;
import com.example.promptsharepro22.data.model.Comment;
import com.example.promptsharepro22.viewmodel.CommentViewModel;
import com.example.promptsharepro22.viewmodel.UserViewModel;

public class UserCommentFragment extends Fragment implements CommentAdapter.OnCommentClickListener {

    private RecyclerView userCommentsRecyclerView;
    private CommentAdapter commentAdapter;
    private CommentViewModel commentViewModel;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_comments, container, false);

        // Initialize UI components
        ImageView backButton = view.findViewById(R.id.backButton);
        userCommentsRecyclerView = view.findViewById(R.id.userCommentsRecyclerView);

        // Set up back button
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Initialize ViewModels
        userViewModel = UserViewModel.getInstance();
        commentViewModel = CommentViewModel.getInstance();

        // Set up RecyclerView with adapter
        userCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentAdapter = new CommentAdapter(null,this);
        userCommentsRecyclerView.setAdapter(commentAdapter);

        // Load comments for the current user
        loadUserComments();

        return view;
    }

    private void loadUserComments() {
        String currentUserId = userViewModel.getUserId().getValue();
        if (currentUserId != null) {
            commentViewModel.getCommentsByUserId(currentUserId).observe(getViewLifecycleOwner(), comments -> {
                if (comments != null) {
                    commentAdapter.setComments(comments);
                }
            });
        }
    }

    @Override
    public void onCommentClick(Comment comment) {
        // Create and load the EditCommentFragment with the selected comment ID
        EditCommentFragment editCommentFragment = new EditCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("commentId", comment.getId());
        editCommentFragment.setArguments(bundle);

        // Replace the current fragment with the EditCommentFragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, editCommentFragment)
                .addToBackStack(null)
                .commit();
    }
}
