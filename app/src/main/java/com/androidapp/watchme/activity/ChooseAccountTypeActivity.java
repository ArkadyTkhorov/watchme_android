package com.androidapp.watchme.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.androidapp.watchme.R;
import com.androidapp.watchme.util.Utils;

public class ChooseAccountTypeActivity extends AppCompatActivity {

    private RelativeLayout userTypeLayout, buddyTypeLayout, userSelectedLayout, buddySelectedLayout;
    private Button confirmBtn;

    private String mName, mEmail, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account_type);

        mName = getIntent().getStringExtra(getString(R.string.name));
        mEmail = getIntent().getStringExtra(getString(R.string.email));
        mPassword = getIntent().getStringExtra(getString(R.string.password));

        userTypeLayout = (RelativeLayout) findViewById(R.id.userTypeLayout);
        userSelectedLayout = (RelativeLayout) findViewById(R.id.userSelectedLayout);
       // buddyTypeLayout = (RelativeLayout) findViewById(R.id.buddyTypeLayout);
       // buddySelectedLayout = (RelativeLayout) findViewById(R.id.buddySelectedLayout);
        confirmBtn = (Button) findViewById(R.id.confirmBtn);

        userTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userTypeLayout.setBackgroundResource(R.drawable.edit_text_background);
                //buddyTypeLayout.setBackgroundResource(R.drawable.unselected_background);
               // buddySelectedLayout.setVisibility(View.GONE);
                userSelectedLayout.setVisibility(View.VISIBLE);
            }
        });

/*        buddyTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buddyTypeLayout.setBackgroundResource(R.drawable.edit_text_background);
                userTypeLayout.setBackgroundResource(R.drawable.unselected_background);
                userSelectedLayout.setVisibility(View.GONE);
                buddySelectedLayout.setVisibility(View.VISIBLE);
            }
        });
*/
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userSelectedLayout.getVisibility() == View.VISIBLE) {
                    finish();
                    Intent intent = new Intent(ChooseAccountTypeActivity.this, AssignBuddyActivity.class);
                    intent.putExtra(getString(R.string.name), mName);
                    intent.putExtra(getString(R.string.email), mEmail);
                    intent.putExtra(getString(R.string.password), mPassword);
                    startActivity(intent);
                } else {
                    Utils.signUpUser(ChooseAccountTypeActivity.this, mName, mEmail, mPassword, getString(R.string.buddy), "");
                }
            }
        });

    }


}
