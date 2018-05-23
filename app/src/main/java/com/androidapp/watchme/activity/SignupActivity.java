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
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button signupBtn;
    private TextView loginTextView;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_signup);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        signupBtn = (Button) findViewById(R.id.signupBtn);
        loginTextView = (TextView) findViewById(R.id.loginTextView);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEditText.getText().toString().isEmpty()) {
                    nameEditText.setError(getString(R.string.input_name));
                    nameEditText.requestFocus();
                    return;
                }
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
                if (passwordEditText.getText().toString().isEmpty()) {
                    passwordEditText.setError(getString(R.string.input_password));
                    passwordEditText.requestFocus();
                    return;
                }
                finish();
                Intent intent = new Intent(SignupActivity.this, ChooseAccountTypeActivity.class);
                intent.putExtra(getString(R.string.name), nameEditText.getText().toString());
                intent.putExtra(getString(R.string.email), emailEditText.getText().toString());
                intent.putExtra(getString(R.string.password), passwordEditText.getText().toString());
                startActivity(intent);
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
