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
import com.example.promptsharepro22.utils.UserInfoUtil;
import com.example.promptsharepro22.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {
    private UserViewModel userViewModel;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UserViewModel scoped to the application
        userViewModel = UserViewModel.getInstance();

        // Initialize components
        ImageView logoImageView = findViewById(R.id.logoImageView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        // Set up login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });

        // Set up signup button click listener to redirect to signup page
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to SignUpActivity
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!UserInfoUtil.isValidEmail(email)) {
            emailEditText.setError("Email must end with @usc.edu");
            return;
        }

        if (!UserInfoUtil.isValidPassword(password)) {
            passwordEditText.setError("Password must be at least 8 characters");
            return;
        }

        userViewModel.loginUser(email, password).observeForever(status -> {
            if (status.equals("Login Successful")) {
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (status.equals("Invalid username or password")) {
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
