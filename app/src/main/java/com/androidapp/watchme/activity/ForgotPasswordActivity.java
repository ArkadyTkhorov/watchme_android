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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordBtn;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        resetPasswordBtn = (Button) findViewById(R.id.resetPasswordBtn);
        loginTextView = (TextView) findViewById(R.id.loginTextView);

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
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

                Utils.forgotPassword(ForgotPasswordActivity.this, emailEditText.getText().toString());

            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
