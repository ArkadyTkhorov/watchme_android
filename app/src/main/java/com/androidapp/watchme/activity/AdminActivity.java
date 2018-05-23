package com.androidapp.watchme.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.androidapp.watchme.R;
import com.androidapp.watchme.util.AdminReciver;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener{

     private DevicePolicyManager watchMeAdmin_PolicyManger ;
     private ComponentName watch_DeviceAdmin;
    private Button watchAdminEnabled_b,cancel_b;
    protected static final int REQUEST_ENABLE = 1;
    protected static final int SET_PASSWORD = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        watchMeAdmin_PolicyManger =(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);

       watch_DeviceAdmin =  new ComponentName(this,
                AdminReciver.class);

       watchAdminEnabled_b = (Button) findViewById(R.id.Enable_b);

       watchAdminEnabled_b.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(watchMeAdmin_PolicyManger.isAdminActive(watch_DeviceAdmin))
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == RESULT_OK)
        {
            switch (resultCode){
                case REQUEST_ENABLE :

                    startActivity(new Intent(getBaseContext(),MainActivity.class));

            }

        }

    }

    @Override
    public void onClick(View v) {

        if (v == watchAdminEnabled_b) {

            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    watch_DeviceAdmin);
            intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                   getResources().getString(R.string.desc));
            startActivityForResult(intent, REQUEST_ENABLE);
        }else if(v == cancel_b)
        {
            // exit the app
        }


    }
}
