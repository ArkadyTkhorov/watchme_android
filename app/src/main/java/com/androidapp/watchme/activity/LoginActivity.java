package com.androidapp.watchme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidapp.watchme.R;
import com.androidapp.watchme.util.Utils;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginBtn;
    private TextView forgotPasswordTextView, signupTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordTextView);
        signupTextView = (TextView) findViewById(R.id.signupTextView);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailEditText.getText().toString().isEmpty()) {
                    emailEditText.setError(getString(R.string.input_email));
                    emailEditText.requestFocus();
                    return;
                }
                if (!Utils.isEmailValid(emailEditText.getText().toString())) {
                    emailEditText.setError(getString(R.string.invalid_email));
                    emailEditText.requestFocus();
                    return;
                }
                if (passwordEditText.getText().toString().isEmpty())
                {
                    passwordEditText.setError(getString(R.string.input_password));
                    passwordEditText.requestFocus();
                    return;
                }

                Utils.loginUser(LoginActivity.this, emailEditText.getText().toString(), passwordEditText.getText().toString());

            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
