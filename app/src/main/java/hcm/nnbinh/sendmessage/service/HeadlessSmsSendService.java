package hcm.nnbinh.sendmessage.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by BinhNguyen on 5/8/2017.
 */

public class HeadlessSmsSendService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
