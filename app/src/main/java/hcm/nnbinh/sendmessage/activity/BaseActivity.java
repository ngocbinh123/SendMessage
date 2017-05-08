package hcm.nnbinh.sendmessage.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import hcm.nnbinh.sendmessage.R;

/**
 * Created by nguyenngocbinh on 5/8/17.
 */

public class BaseActivity extends AppCompatActivity {
    public static final int CHANGE_DEFAULT_APP = 121;
    private static final String TAG = BaseActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void checkDefaultApp() {
        String defaultApp = getPackageNameOfDefaultApp();
        if (!defaultApp.equals(getPackageName())) {
            requestChangeDefaultApp();
        }
    }

    protected void requestChangeDefaultApp() {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
        startActivityForResult(intent, CHANGE_DEFAULT_APP);
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

    protected void loadImageQuickly(String url, ImageView imageView) {
        Glide.with(BaseActivity.this).load(url)
                .error(R.mipmap.ic_camera)
                .placeholder(R.mipmap.ic_camera)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    protected void showNotification(String message) {
        Toast.makeText(getApplicationContext(),R.string.noti_sent_complete, Toast.LENGTH_SHORT).show();
    }
}
