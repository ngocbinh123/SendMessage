package hcm.nnbinh.sendmessage.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.klinker.android.send_message.ApnUtils;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;
import com.droidprism.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hcm.nnbinh.sendmessage.MMS.SettingApp;
import hcm.nnbinh.sendmessage.MainActivity;
import hcm.nnbinh.sendmessage.R;
import hcm.nnbinh.sendmessage.MMS.APNModel;

/**
 * Created by nguyenngocbinh on 4/29/17.
 */

public class ChatRoomActivity extends BaseActivity {
    private final static String TAG = ChatRoomActivity.class.getName();
    private static final int PICK_PHOTO_FOR_AVATAR = 111;
    private String mImagePath = "";
    private String mPhoneNumber = "";
    private SmsManager mManager;
    private SettingApp settingApp;

    private  int mcc = 0;
    private  int mnc = 0;

    @BindView(R.id.txt_sms)
    EditText txtSMS;

    @BindView(R.id.act_add_image)
    ImageView actAddImage;
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
        if (requestCode == PICK_PHOTO_FOR_AVATAR) {
            if (resultCode == RESULT_OK && data != null) {
                mImagePath = getImageFromUri(data.getData());
                loadImageQuickly(mImagePath,actAddImage);
                Log.d(TAG, "SEND MSS: picked image: " + mImagePath);
            }else {
                clearImageView();
                return;
            }
        }
    }

    @OnClick(R.id.act_send)
    void sendMessage() {
        if (mImagePath == "")
            sendSMS();
        else
            sendMMS(mImagePath, txtSMS.getText().toString());
        refreshUI();

    }

    private void sendSMS() {
        String message = txtSMS.getText().toString();
        mManager.sendTextMessage(mPhoneNumber, null, message, null, null);
    }

    @OnClick(R.id.act_add_image)
    void onClickChooseImage() {
        pickImage();
    }

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

    private void refreshUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clearImageView();
                txtSMS.setText("");
            }
        });
    }

    private void clearImageView() {
        mImagePath = "";
        actAddImage.setImageResource(R.mipmap.ic_camera);
    }

    private void sendMMS(final String imagePath, final String content) {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                Settings sendSettings = new Settings();
                APNModel apn = getApn();
                sendSettings.setMmsc(apn.getMmsc());
                sendSettings.setProxy(apn.getProxy());
                sendSettings.setPort(apn.getPort());

                sendSettings.setUseSystemSending(true);

                Transaction transaction = new Transaction(ChatRoomActivity.this,sendSettings);

                Message message = new Message(content, mPhoneNumber);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                message.setImage(bitmap);
                transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                showNotification(getString(R.string.noti_sent_complete));
                refreshUI();
            }
        }.execute();
    }
}
