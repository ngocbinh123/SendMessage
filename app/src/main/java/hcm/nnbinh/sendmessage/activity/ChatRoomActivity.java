package hcm.nnbinh.sendmessage.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.droidprism.APN;
import com.google.android.mms.*;
import com.klinker.android.send_message.ApnUtils;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;
import com.droidprism.*;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hcm.nnbinh.sendmessage.MMS.SettingApp;
import hcm.nnbinh.sendmessage.MainActivity;
import hcm.nnbinh.sendmessage.R;
import hcm.nnbinh.sendmessage.sqlite.APNModel;
import hcm.nnbinh.sendmessage.sqlite.DatabaseHelper;

/**
 * Created by nguyenngocbinh on 4/29/17.
 */

public class ChatRoomActivity extends AppCompatActivity {
    private final static String TAG = "ChatRoomActivity";
    private static final int PICK_PHOTO_FOR_AVATAR = 111;
    @BindView(R.id.txt_sms)
    EditText txtSMS;
    private String mPhoneNumber = "";
    private SmsManager mManager;
    private SettingApp settingApp;
    private int count = 1;
    private  int mcc = 0;
    private  int mnc = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mPhoneNumber = intent.getStringExtra(MainActivity.PHONE_NUMBER);
        setTitle(mPhoneNumber);
        mManager = SmsManager.getDefault();
        initSettings();

        TelephonyManager tel = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc= Integer.parseInt(networkOperator.substring(3));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == RESULT_OK) {
            if (data == null) {
                //Display an error
                Log.d(TAG, "SEND MSS: can not pick image");
                return;
            }
            String path= getImageFromUri(data.getData());
            Log.d(TAG, "SEND MSS: picked image: " + path);
            txtSMS.setText("send MSS " + String.valueOf(count++));
            //sendData(path);
            sendMMS(path);
           }
    }

    @OnClick(R.id.act_send)
    void sendSMS() {
        String message = txtSMS.getText().toString();
        mManager.sendTextMessage(mPhoneNumber, null, message, null, null);
    }

    @OnClick(R.id.act_add_image)
    void onClickChooseImage() {
        pickImage();
    }
//
//    private void sendMMS(String imagePath) {
//        Uri uri = Uri.parse(imagePath);
//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.putExtra("address",mPhoneNumber);
//        i.putExtra("sms_body","This is the text mms");
//        i.putExtra(Intent.EXTRA_STREAM,"file:/"+uri);
//        i.setType("image/png");
//        startActivity(i);
//    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    private String getImageFromUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        return picturePath;
    }

    private void initSettings() {
        settingApp = SettingApp.get(this);
        if (TextUtils.isEmpty(settingApp.getMmsc()) &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            initApns();
        }
    }

    private void initApns() {
        ApnUtils.initDefaultApns(this, new ApnUtils.OnApnFinishedListener() {
            @Override
            public void onFinished() {
                settingApp = SettingApp.get(ChatRoomActivity.this, true);
            }
        });
    }

    private APNModel getApn() {
        APNModel apn= null;
        Carrier c = Carrier.getCarrier(mcc, mnc);
        if (c != null) {
            com.droidprism.APN a = c.getAPN();
            if (a != null) {
                apn = new APNModel("mms", a.mmsc, a.proxy, String.valueOf(a.port));
            }
        }
        return apn;
    }

    public void sendData(String imagePath){
        Intent mmsIntent = new Intent(Intent.ACTION_SEND);
        mmsIntent.putExtra("sms_body", "text");
        mmsIntent.putExtra("address", mPhoneNumber);
        mmsIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
        mmsIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(mmsIntent, "Send"));

    }

    private void sendMMS(final String imagePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Settings sendSettings = new Settings();
                APNModel apn = getApn();
                sendSettings.setMmsc(apn.getMmsc());
                sendSettings.setProxy(apn.getProxy());
                sendSettings.setPort(apn.getPort());

                sendSettings.setUseSystemSending(true);

                Transaction transaction = new Transaction(ChatRoomActivity.this,sendSettings);

                Message message = new Message(txtSMS.getText().toString(), mPhoneNumber);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                message.setImage(bitmap);
                transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
            }
        }).start();
    }
}
