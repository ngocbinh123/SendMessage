package hcm.nnbinh.sendmessage.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import hcm.nnbinh.sendmessage.MainActivity;

/**
 * Created by nguyenngocbinh on 5/7/17.
 */

public class PermissionActivity extends AppCompatActivity {
    private static final String TAG = PermissionActivity.class.getName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.RECEIVE_MMS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CHANGE_NETWORK_STATE
            }, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("request_permissions", false)
                .commit();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
