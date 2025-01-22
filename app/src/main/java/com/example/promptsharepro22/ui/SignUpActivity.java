package com.example.promptsharepro22.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.promptsharepro22.R;
import com.example.promptsharepro22.data.model.User;
import com.example.promptsharepro22.utils.TimeUtil;
import com.example.promptsharepro22.utils.UserInfoUtil;
import com.example.promptsharepro22.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {
    private UserViewModel userViewModel;

    private EditText usernameEditText;
    private EditText uscIdEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signupButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UserViewModel scoped to the application
        userViewModel = UserViewModel.getInstance();

        // Initialize components
        ImageView logoImageView = findViewById(R.id.logoImageView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        usernameEditText = findViewById(R.id.usernameEditText);
        uscIdEditText = findViewById(R.id.uscIdEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupButton = findViewById(R.id.signupButton);
        loginButton = findViewById(R.id.loginButton);

        // Set up sign up button click listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSignUp();
            }
        });

        // Set up login button click listener to redirect to login page
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to LoginActivity
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void handleSignUp() {
        String username = usernameEditText.getText().toString().trim();
        String uscId = uscIdEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!UserInfoUtil.isValidUsername(username)) {
            usernameEditText.setError("Username must be at least 3 characters");
            return;
        }

        if (!UserInfoUtil.isValidUscId(uscId)) {
            uscIdEditText.setError("USC ID must be 10 characters");
            return;
        }

        if (!UserInfoUtil.isValidEmail(email)) {
            emailEditText.setError("Email must end with @usc.edu");
            return;
        }

        if (!UserInfoUtil.isValidPassword(password)) {
            passwordEditText.setError("Password must be at least 8 characters");
            return;
        }

        // Create a new user object
        String createdAt = TimeUtil.getCurrentDate();
        User user = new User(username, password, email, uscId, createdAt);

        // Register the user
        userViewModel.registerUser(user).observeForever(status -> {
            if (status.equals("Registration Successful")) {
                Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (status.equals("Registration Failed")) {
                Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
