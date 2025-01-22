package com.example.promptsharepro22.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.example.promptsharepro22.R;
import com.example.promptsharepro22.data.model.Post;
import com.example.promptsharepro22.viewmodel.PostViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements PostAdapter.OnPostClickListener {

    private PostViewModel postViewModel;
    private PostAdapter postAdapter;
    private RecyclerView searchResultsRecyclerView;
    private String selectedSearchType = "all"; // Default search type

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        setupSearchTypeSpinner(view);

        EditText searchEditText = view.findViewById(R.id.searchEditText);
        Button searchButton = view.findViewById(R.id.searchButton);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);

        // Pass 'this' as the click listener to the PostAdapter
        postAdapter = new PostAdapter(new ArrayList<>(), this);
        searchResultsRecyclerView.setAdapter(postAdapter);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postViewModel = PostViewModel.getInstance();

        // Handle search button click
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                Log.d("SearchFragment", "Performing search with query: " + query);
                performSearch(query);
            } else {
                Log.d("SearchFragment", "Search query is empty");
            }
        });

        return view;
    }

    private void setupSearchTypeSpinner(View view) {
        Spinner searchTypeSpinner = view.findViewById(R.id.searchTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.search_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchTypeSpinner.setAdapter(adapter);

        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSearchType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSearchType = "all";
            }
        });
    }

    private void performSearch(String query) {
        postViewModel.searchPosts(query, selectedSearchType).observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                postAdapter.setPosts(posts);
            }
        });
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
}
