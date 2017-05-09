package hcm.nnbinh.sendmessage.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import hcm.nnbinh.sendmessage.R;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by nguyenngocbinh on 5/8/17.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getName();
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String PERMISSION_SEND_SMS = "PERMISSION_SEND_SMS";
    public static final String PERMISSION_PHONE_NETWORK = "PERMISSION_PHONE_NETWORK";
    public static final int CHANGED_DEFAULT_APP = 121;
    public static final int REQUEST_WRITE_STORAGE = CHANGED_DEFAULT_APP + 1;
    public static final int REQUEST_PHONE_NETWORK = REQUEST_WRITE_STORAGE + 1;
    public static final int REQUEST_SEND_RECEIVE_SMS = REQUEST_PHONE_NETWORK + 1;
    public static final int REQUEST_ALL_PERMISSION = REQUEST_SEND_RECEIVE_SMS + 1;
    private SharedPreferences mShared;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShared = PreferenceManager.getDefaultSharedPreferences(this);
    }

    protected boolean isNeedChangeDefaultApp() {
        String defaultApp = getPackageNameOfDefaultApp();
        if (!defaultApp.equals(getPackageName()))
            return true;
        return false;
    }

    protected void requestChangeDefaultApp() {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
        startActivityForResult(intent, CHANGED_DEFAULT_APP);
    }

    protected String getPackageNameOfDefaultApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            return Telephony.Sms.getDefaultSmsPackage(BaseActivity.this);
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms");
            final List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, 0);
            if (resolveInfos != null && !resolveInfos.isEmpty())
                return resolveInfos.get(0).activityInfo.packageName;
            return null;
        }
    }


    protected boolean isNeedRequestPerWriteStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
            return true;
        return false;
    }

    protected void requestPerWriteStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE }, REQUEST_WRITE_STORAGE);
    }

    protected boolean isNeedRequestSendSMS() {
        if (mShared.getBoolean(PERMISSION_SEND_SMS, true) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return true;
        return false;
    }

    protected boolean isNeedRequestPhoneNetwork() {
        if (mShared.getBoolean(PERMISSION_PHONE_NETWORK, true) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return true;
        return false;
    }

    protected void requestPerSendSMS(boolean isSMS) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isSMS)
                requestPermissions(new String[]{
                        Manifest.permission.READ_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.RECEIVE_MMS
                }, REQUEST_SEND_RECEIVE_SMS);
            else
                if (!mShared.getBoolean(PERMISSION_SEND_SMS, true))
                    requestPermissions(new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CHANGE_NETWORK_STATE
                    }, REQUEST_PHONE_NETWORK);
                else
                    requestPermissions(new String[]{
                            Manifest.permission.READ_SMS,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.RECEIVE_MMS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CHANGE_NETWORK_STATE
                    }, REQUEST_ALL_PERMISSION);
        }
    }

    protected void loadImageQuickly(String url, ImageView imageView) {
        try {
            Glide.with(BaseActivity.this).load(url)
                    .error(R.mipmap.ic_camera)
                    .placeholder(R.mipmap.ic_camera)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected void showNotification(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }
}
