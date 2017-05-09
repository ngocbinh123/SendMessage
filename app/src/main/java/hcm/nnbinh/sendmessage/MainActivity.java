package hcm.nnbinh.sendmessage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hcm.nnbinh.sendmessage.activity.BaseActivity;
import hcm.nnbinh.sendmessage.activity.ChatRoomActivity;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getName();
    @BindView(R.id.txt_phone_number)
    TextView txtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
}
