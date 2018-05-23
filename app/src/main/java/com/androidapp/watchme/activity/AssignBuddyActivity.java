package com.androidapp.watchme.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidapp.watchme.R;
import com.androidapp.watchme.util.Utils;

public class AssignBuddyActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button assignBtn;

    private String mName, mEmail, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_buddy);

        mName = getIntent().getStringExtra(getString(R.string.name));
        mEmail = getIntent().getStringExtra(getString(R.string.email));
        mPassword = getIntent().getStringExtra(getString(R.string.password));

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        assignBtn = (Button) findViewById(R.id.assignBuddyBtn);

        assignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailEditText.getText().toString().isEmpty()) {
                    emailEditText.setError(getString(R.string.input_buddy_email));
                    emailEditText.requestFocus();
                    return;
                }
                if (!Utils.isEmailValid(emailEditText.getText().toString())) {
                    emailEditText.setError(getString(R.string.invalid_email));
                    emailEditText.requestFocus();
                    return;
                }
                Utils.signUpUser(AssignBuddyActivity.this, mName, mEmail, mPassword, getString(R.string.user), emailEditText.getText().toString());
            }
        });
    }
}
