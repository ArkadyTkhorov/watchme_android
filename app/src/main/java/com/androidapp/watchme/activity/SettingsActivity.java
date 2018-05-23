package com.androidapp.watchme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapp.watchme.R;
import com.androidapp.watchme.util.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import static com.androidapp.watchme.util.MyApplication.mFirebaseDatabaseUsers;
import static com.androidapp.watchme.util.MyApplication.utils;

public class SettingsActivity extends AppCompatActivity {

    private TextView backTextView, saveTextView, emailTextView, resetPasswordTextView;
    private EditText nameEditText, buddyEditText;
    private LinearLayout buddyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backTextView = (TextView) findViewById(R.id.backTextView);
        saveTextView = (TextView) findViewById(R.id.saveTextView);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        resetPasswordTextView = (TextView) findViewById(R.id.resetPasswordTextView);
        buddyEditText = (EditText) findViewById(R.id.buddyEditText);
        buddyLayout = (LinearLayout) findViewById(R.id.buddyLayout);

        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEditText.getText().toString().isEmpty()) {
                    nameEditText.setError(getString(R.string.input_name));
                    nameEditText.requestFocus();
                    return;
                }
                if (buddyEditText.getText().toString().isEmpty()) {
                    buddyEditText.setError(getString(R.string.input_buddy_email));
                    buddyEditText.requestFocus();
                    return;
                }
                if (!Utils.isEmailValid(buddyEditText.getText().toString())) {
                    buddyEditText.setError(getString(R.string.invalid_email));
                    buddyEditText.requestFocus();
                    return;
                }

                mFirebaseDatabaseUsers.orderByChild(getString(R.string.email)).equalTo(emailTextView.getText().toString()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        mFirebaseDatabaseUsers.child(dataSnapshot.getKey()).child(getString(R.string.name)).setValue(nameEditText.getText().toString());
                        mFirebaseDatabaseUsers.child(dataSnapshot.getKey()).child(getString(R.string.buddy)).setValue(buddyEditText.getText().toString());

                        utils.saveToPreference(SettingsActivity.this, getString(R.string.name), nameEditText.getText().toString());
                        utils.saveToPreference(SettingsActivity.this, getString(R.string.buddy), buddyEditText.getText().toString());

                        Toast.makeText(SettingsActivity.this, getString(R.string.profile_updated), Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        nameEditText.setText(utils.getFromPreference(SettingsActivity.this, getString(R.string.name), ""));
        emailTextView.setText(utils.getFromPreference(SettingsActivity.this, getString(R.string.email), ""));

        resetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(SettingsActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        buddyEditText.setText(utils.getFromPreference(SettingsActivity.this, getString(R.string.buddy), ""));

        if (utils.getFromPreference(SettingsActivity.this, getString(R.string.user_type), "").equals("Buddy")) {
            buddyLayout.setVisibility(View.GONE);
        } else {
            buddyLayout.setVisibility(View.VISIBLE);
        }

    }
}
