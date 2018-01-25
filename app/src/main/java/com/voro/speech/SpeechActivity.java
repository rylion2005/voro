package com.voro.speech;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.content.pm.PackageManager;
import android.util.Log;
import java.util.ArrayList;
import com.voro.speech.utils.IntegrationCheck;


/*
*  This class is a top speech activity
*    It does:
*      1. device permission check
*      2. integration check
*
*/
public class SpeechActivity extends AppCompatActivity {
    private static final String TAG = "SpeechActivity";

    public static final int PERMISSION_REQUEST_CODE = 0xA00F;

    protected Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new SpeechHandler();
        initPermissions();
        integrationCheck();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Log.e(TAG, "Not all permissions are granted");
                        }
                    }
                } else {
                    Log.e(TAG, "unknown permission error");
                }
                break;
            default:
        }
    }

    /**
     *  >android 6.0: permission granted must be allowed
     */
    private void initPermissions() {
        final String[] PERMISSIONS = {
                Manifest.permission.INTERNET,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        Log.i(TAG, "Init permissions");

        ArrayList<String> permissionList = new ArrayList<String>();
        for (String perm : PERMISSIONS) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                permissionList.add(perm);
            }
        }

        if ( !permissionList.isEmpty() ) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }else{
            Log.i(TAG, "all permissions are granted!");
        }
    }

    private void integrationCheck(){
        IntegrationCheck.getInstance(getApplicationContext()).check(mHandler);
    }

    class SpeechHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage: " + msg.what);
            if (msg.what == 100) {
                Log.i(TAG, msg.obj.toString());
            }
        }
    }
}
