package com.example.promptsharepro22.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.promptsharepro22.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class UserDataAccess {
    private final DatabaseReference databaseRef;

    public UserDataAccess() {
        databaseRef = FirebaseDatabase.getInstance().getReference("users");
    }

    // Register a new user with auto-generated ID
    public LiveData<String> register(User user) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        DatabaseReference newUserRef = databaseRef.push(); // Generates a unique key
        user.setId(newUserRef.getKey()); // Set the generated ID on the User object
        newUserRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(user.getId()); // Return the assigned ID
            } else {
                liveData.setValue(null); // Indicate failure
            }
        });
        return liveData;
    }

    public LiveData<User> getUser(String id) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        databaseRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                liveData.setValue(user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                liveData.setValue(null); // Handle error if needed
            }
        });
        return liveData;
    }

    // Retrieve user by email
    public LiveData<User> getUserByEmail(String email) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Query the database for the user with the specified email
        databaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        userLiveData.setValue(user);
                        break; // Assuming emails are unique, so we break after finding the first match
                    }
                } else {
                    userLiveData.setValue(null); // No user found with this email
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                userLiveData.setValue(null); // Handle database error
            }
        });

        return userLiveData;
    }

    public LiveData<Boolean> updateProfile(String id, User user) {
        Map<String, Object> updates = user.buildUpdateMap();
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        databaseRef.child(id).updateChildren(updates).addOnCompleteListener(task -> {
            liveData.setValue(task.isSuccessful());
        });
        return liveData;
    }

    public LiveData<Boolean> deleteAccount(String id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        databaseRef.child(id).removeValue().addOnCompleteListener(task -> {
            liveData.setValue(task.isSuccessful());
        });
        return liveData;
    }
}
