package com.example.promptsharepro22.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.promptsharepro22.viewmodel.PostViewModel;
import com.example.promptsharepro22.R;

public class HomeFragment extends Fragment implements PostAdapter.OnPostClickListener {

    private PostAdapter postAdapter;
    private PostViewModel postViewModel;
    private OnSearchButtonClickListener searchButtonClickListener;

    public interface OnSearchButtonClickListener {
        void onSearchButtonClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchButtonClickListener) {
            searchButtonClickListener = (OnSearchButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSearchButtonClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchButtonClickListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Pass 'this' as the click listener to the PostAdapter
        postAdapter = new PostAdapter(null, this);
        postsRecyclerView.setAdapter(postAdapter);

        // Initialize ViewModel
        postViewModel = PostViewModel.getInstance();

        // Observe posts
        postViewModel.getAllPosts().observe(getViewLifecycleOwner(), posts -> postAdapter.setPosts(posts));

        ImageButton searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            if (searchButtonClickListener != null) {
                searchButtonClickListener.onSearchButtonClick();
            }
        });

        setupFilterListeners(view);

        return view;
    }

    @Override
    public void onPostClick(String postId) {
        // Create and load the PostDetailFragment
        PostDetailFragment postDetailFragment = new PostDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("postId", postId); // Pass the post ID
        postDetailFragment.setArguments(bundle);

        // Replace the current fragment with PostDetailFragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, postDetailFragment)
                .addToBackStack(null) // Add to back stack for navigation
                .commit();
    }

    private void setupFilterListeners(View view) {
        Spinner sortSpinner = view.findViewById(R.id.sortSpinner);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] sortOptions = getResources().getStringArray(R.array.sort_options);
                String selectedSort = sortOptions[position];
                String sortBy = null;

                // Map user-friendly options to database fields
                switch (selectedSort) {
                    case "Sort by Most Recent":
                        sortBy = "createdAt";
                        break;
                    case "Sort by Title":
                        sortBy = "title";
                        break;
                    case "Sort by LLM Kind":
                        sortBy = "llmKind";
                        break;
                    default:
                        sortBy = null; // Default sorting
                        break;
                }

                postViewModel.getPostsSortedBy(sortBy).observe(getViewLifecycleOwner(), posts -> postAdapter.setPosts(posts));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
}
