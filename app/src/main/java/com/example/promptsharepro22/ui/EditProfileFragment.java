package com.example.promptsharepro22.ui;

import android.graphics.drawable.Drawable;
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
import com.example.promptsharepro22.data.model.User;
import com.example.promptsharepro22.utils.AvaterUtil;
import com.example.promptsharepro22.viewmodel.UserViewModel;

public class EditProfileFragment extends Fragment {

    private ImageView profileImageView;
    private EditText firstNameEditText, lastNameEditText, usernameEditText, emailEditText, passwordEditText, uscIdEditText;
    private Button updateButton;
    private UserViewModel userViewModel;
    private ImageView backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize Views
        profileImageView = view.findViewById(R.id.profilePicture);
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        uscIdEditText = view.findViewById(R.id.uscIdEditText);
        updateButton = view.findViewById(R.id.updateButton);
        backButton = view.findViewById(R.id.backButton);

        // Initialize ViewModel
        userViewModel = UserViewModel.getInstance();

        // Load user data
        loadUserData();

        // Set click listeners
        backButton.setOnClickListener(v -> backToProfile());
        updateButton.setOnClickListener(v -> updateProfile());

        return view;
    }

    private void loadUserData() {
        userViewModel.getUser(userViewModel.getUserId().getValue()).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                firstNameEditText.setText(user.getFirstName());
                lastNameEditText.setText(user.getLastName());
                usernameEditText.setText(user.getUsername());
                emailEditText.setText(user.getEmail());
                passwordEditText.setText(user.getPassword());
                uscIdEditText.setText(user.getUSCID());

                // Set profile image
                Drawable profileImage = AvaterUtil.getInitialsDrawable(user.getFirstName(), user.getLastName());
                profileImageView.setImageDrawable(profileImage);
            }
        });
    }

    private void updateProfile() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String uscId = uscIdEditText.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || uscId.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = userViewModel.getUserId().getValue();
        User updatedUser = new User();
        updatedUser.setFirstName(firstName);
        updatedUser.setLastName(lastName);
        updatedUser.setUsername(username);
        updatedUser.setEmail(email);
        updatedUser.setPassword(password);
        updatedUser.setUSCID(uscId);

        userViewModel.updateProfile(currentUserId, updatedUser).observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                backToProfile();
            } else {
                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void backToProfile() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
