package hcm.nnbinh.sendmessage;

import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hcm.nnbinh.sendmessage.activity.BaseActivity;
import hcm.nnbinh.sendmessage.activity.ChatRoomActivity;
import hcm.nnbinh.sendmessage.activity.PermissionActivity;

public class MainActivity extends BaseActivity {

    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String TAG = MainActivity.class.getName();
    private static final int CHANGE_DEFAULT_APP = 121;
    @BindView(R.id.txt_phone_number)
    TextView txtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkDefaultApp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_DEFAULT_APP)
            if (resultCode == RESULT_OK)
                checkPermission();
            else
                checkDefaultApp();
    }

    @OnClick(R.id.act_start)
    void onClickSButtonStart() {
        String txtNumber = txtPhoneNumber.getText().toString().trim();
        if (!checkPhoneNumber(txtNumber)) return;

        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra(PHONE_NUMBER, txtNumber);
        startActivity(intent);
    }

    private boolean checkPhoneNumber(String num) {
        if (num.isEmpty())
            return false;
        if (num.length() < 10)
            return false;
        return true;
    }

    private void checkPermission() {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("request_permissions", true) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startActivity(new Intent(this, PermissionActivity.class));
            finish();
            return;
        }
    }
}
