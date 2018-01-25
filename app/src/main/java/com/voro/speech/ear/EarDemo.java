package com.voro.speech.ear;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.Map;
import com.voro.R;
import com.voro.speech.SpeechActivity;


public class EarDemo extends SpeechActivity implements View.OnLongClickListener, View.OnTouchListener, EventListener{
    private static final String TAG = "EarDemo";

    private TextView mTxvResults;
    private MyRecognizer mRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ear_demo);
        initViews();

        mRecognizer = new MyRecognizer(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecognizer.release();
    }

    @Override
    public boolean onLongClick(View v) {
        boolean processed = false;

        switch (v.getId()){
            case R.id.BTN_StartRecord:
                mRecognizer.start();
                processed = true;
                break;

            default:
                break;
        }

        return processed;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean processed = false;

        if (v.getId() == R.id.BTN_StartRecord){
            if (event.getAction() == MotionEvent.ACTION_UP){
                processed = true;
                mRecognizer.stop();
            }
        }
        return processed;
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        Log.i(TAG, "onEvent: ");
        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "name: " + name);
        Log.i(TAG, "params: " + params);
        Log.i(TAG, "data: " + data);
        Log.i(TAG, "offset: " + offset + "; length: " + length);
        Log.i(TAG, "--------------------------------------------------");

        mTxvResults.append("\n" + RecognitionResult.getRecognitionFinalBestResult(params));
    }

    private void initViews(){
        mTxvResults = findViewById(R.id.TXV_RecognizeResults);
        mTxvResults.setMovementMethod(ScrollingMovementMethod.getInstance());

        Button btnRecord = findViewById(R.id.BTN_StartRecord);
        btnRecord.setOnLongClickListener(this);
        btnRecord.setOnTouchListener(this);
    }
}
