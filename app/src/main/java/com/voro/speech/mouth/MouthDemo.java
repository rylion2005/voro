package com.voro.speech.mouth;

import android.os.Message;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.voro.R;
import com.voro.speech.SpeechActivity;


public class MouthDemo extends SpeechActivity implements View.OnClickListener {
    private static final String TAG = "MouthDemo";

    private static final String TEXT = "大家好,请叫我月宫玉兔!";
    private MySynthesizer mSynthesizer;
    protected Handler mHandler;
    private EditText mEdtInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mouth_demo);

        initViews();
        mHandler = new MyHandler();
        mSynthesizer = new MySynthesizer(this, mHandler);
    }

    @Override
    protected void onDestroy() {
        mSynthesizer.release();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.BTN_StartSpeech:
                String speechText;
                if ((mEdtInput == null) || (mEdtInput.getText().length() == 0)){
                    speechText = TEXT;
                } else {
                    speechText = mEdtInput.getText().toString();
                }
                mSynthesizer.speak(speechText);
                break;

            case R.id.BTN_StopSpeech:
                mSynthesizer.stop();
                break;

            default:
                break;
        }
    }

    private void initViews(){
        mEdtInput = findViewById(R.id.EDT_InputText);
        Button btnStart = findViewById(R.id.BTN_StartSpeech);
        Button btnStop = findViewById(R.id.BTN_StopSpeech);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage: " + msg.what);
            if (msg.what == MySynthesizer.MSG_PRINT) {
                Log.i(TAG, (String) msg.obj);
            }
        }
    }
}
