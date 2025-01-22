package com.example.promptsharepro22.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.promptsharepro22.data.model.User;
import com.example.promptsharepro22.data.repository.UserDataAccess;

public class UserViewModel {
    private static UserViewModel instance;
    private final UserDataAccess userDataAccess;
    private final MutableLiveData<String> loginStatus;
    private final MutableLiveData<String> userId;

    public UserViewModel() {
        userDataAccess = new UserDataAccess();
        loginStatus = new MutableLiveData<>();
        userId = new MutableLiveData<>();
    }

    public static synchronized UserViewModel getInstance() {
        if (instance == null) {
            instance = new UserViewModel();
        }
        return instance;
    }

    // Register a new user
    public LiveData<String> registerUser(User user) {
        userDataAccess.register(user).observeForever(newUserId -> {
            if (newUserId != null) {
                loginStatus.setValue("Registration Successful");
                userId.setValue(newUserId);
            } else {
                loginStatus.setValue("Registration Failed");
            }
        });
        return loginStatus;
    }

    // Login existing user
    public LiveData<String> loginUser(String email, String password) {
        userDataAccess.getUserByEmail(email).observeForever(fetchedUser -> {
            if (fetchedUser != null && fetchedUser.getPassword().equals(password)) {
                loginStatus.setValue("Login Successful");
                userId.setValue(fetchedUser.getId());
            } else {
                loginStatus.setValue("Invalid username or password");
            }
        });
        return loginStatus;
    }

    // Get user by ID
    public LiveData<User> getUser(String id) {
        return userDataAccess.getUser(id);
    }

    // New method to get user's email by user ID
    public LiveData<String> getUserEmail(String userId) {
        MutableLiveData<String> emailLiveData = new MutableLiveData<>();
        userDataAccess.getUser(userId).observeForever(user -> {
            if (user != null) {
                emailLiveData.setValue(user.getEmail());
            } else {
                emailLiveData.setValue("Unknown");
            }
        });
        return emailLiveData;
    }

    // Update user profile
    public LiveData<Boolean> updateProfile(String id, User user) {
        return userDataAccess.updateProfile(id, user);
    }

    // Delete user account
    public LiveData<Boolean> deleteUser(String id) {
        return userDataAccess.deleteAccount(id);
    }

    // Observe the currently logged-in user ID
    public LiveData<String> getUserId() {
        return userId;
    }

    // Logout the user
    public void logout() {
        userId.setValue(null);
    }
}
