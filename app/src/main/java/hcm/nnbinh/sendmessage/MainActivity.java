package hcm.nnbinh.sendmessage;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hcm.nnbinh.sendmessage.activity.ChatRoomActivity;
import hcm.nnbinh.sendmessage.activity.PermissionActivity;

public class MainActivity extends AppCompatActivity {

    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    @BindView(R.id.txt_phone_number)
    TextView txtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("request_permissions", true) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startActivity(new Intent(this, PermissionActivity.class));
            finish();
            return;
        }
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
        return true;
    }

}
