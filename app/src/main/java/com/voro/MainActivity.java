package com.voro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.voro.brain.BrainDemo;
import com.voro.speech.SpeechDemo;
import com.voro.speech.ear.EarDemo;
import com.voro.speech.mouth.MouthDemo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initViews();
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: " + v);
        switch (v.getId()){
            case R.id.BTN_GoBrainDemo:
                startActivity(new Intent(this, BrainDemo.class));
                break;
            case R.id.BTN_GoSpeechDemo:
                startActivity(new Intent(this, SpeechDemo.class));
                break;
            case R.id.BTN_GoEarDemo:
                startActivity(new Intent(this, EarDemo.class));
                break;
            case R.id.BTN_GoMouthDemo:
                startActivity(new Intent(this, MouthDemo.class));
                break;
            default:
                break;
        }
    }

    public void initViews(){
        Button BtnGoBrain = (Button) findViewById(R.id.BTN_GoBrainDemo);
        BtnGoBrain.setOnClickListener(this);
        Button BtnGoSpeech = (Button) findViewById(R.id.BTN_GoSpeechDemo);
        BtnGoSpeech.setOnClickListener(this);
        Button BtnGoEar = (Button) findViewById(R.id.BTN_GoEarDemo);
        BtnGoEar.setOnClickListener(this);
        Button BtnGoMouth = (Button) findViewById(R.id.BTN_GoMouthDemo);
        BtnGoMouth.setOnClickListener(this);
    }
}
