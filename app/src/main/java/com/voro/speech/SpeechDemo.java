package com.voro.speech;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.baidu.speech.EventListener;
import com.voro.R;
import com.voro.speech.ear.MyRecognizer;
import com.voro.speech.ear.RecognitionResult;
import com.voro.speech.mouth.MySynthesizer;

public class SpeechDemo extends SpeechActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener, EventListener {

    private static final String TAG = "SpeechDemo";
    private static final String WELCOME_TEXT = "大家好, 请叫我月宫小兔子!";
    private static String mSpeechText;

    protected Handler mHandler;
    private MySynthesizer mSynthesizer;
    private TextView mTxvResults;
    private MyRecognizer mRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech_demo);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler = new MyHandler();
        mSynthesizer = new MySynthesizer(this, mHandler);
        mRecognizer = new MyRecognizer(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRecognizer.release();
        mSynthesizer.release();
        mSpeechText = null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        boolean processed = false;

        switch (v.getId()){
            case R.id.BTN_Record:
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

        if (v.getId() == R.id.BTN_Record){
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

        String speechText = RecognitionResult.getRecognitionFinalBestResult(params);
        if ((speechText != null) && (speechText.length() != 0)){
            mSpeechText = speechText;
            mTxvResults.setText(speechText);
        }

        if (name.equals("asr.finish")){
            String text;
            if (mSpeechText == null){
                text = "对不起， 我没听清楚您说的什么？";
            }else{
                text = "您说的是不是：" + mSpeechText;
            }
            mSynthesizer.speak(text);
        }
    }

    private void initViews(){
        mTxvResults = findViewById(R.id.TXV_Results);
        Button btnRecord = findViewById(R.id.BTN_Record);
        btnRecord.setOnLongClickListener(this);
        btnRecord.setOnTouchListener(this);
    }

    static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage: " + msg.what);
            if (msg.what == MySynthesizer.MSG_PRINT) {
                Log.i(TAG, (String) msg.obj);
            }
        }
    }
}
