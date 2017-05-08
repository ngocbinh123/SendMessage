package hcm.nnbinh.sendmessage.sqlite;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

/**
 * Created by nguyenngocbinh on 5/7/17.
 */

public class DatabaseHelper {
    private ContentResolver mResolver;

    public DatabaseHelper(Context context) {
        mResolver = context.getContentResolver();
    }

    public static DatabaseHelper get(Context context) {
        return  new DatabaseHelper(context);
    }

    public APNModel getAPN() {
        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            cursor = mResolver.query(Uri.withAppendedPath(Telephony.Carriers.CONTENT_URI, "current"),
                    null, null, null, null);
        }else {

        }
        cursor.moveToLast();
        return  new APNModel(cursor);
    }
}
