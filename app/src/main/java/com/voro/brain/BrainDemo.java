package com.voro.brain;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import com.voro.R;


public class BrainDemo extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "BrainDemo";

    // Training result file
    private static final String MODEL_FILE = "file:///android_asset/graphdef.pb";

    /*
    ** tensor flow input/output node name
    **   We should keep accordance with ones in your training model
    */
    private static final String INPUT_NODE = "encoder4";
    private static final String OUTPUT_NODE = "decoder4";
    /*
    ** tensor dimensions define in training model
    */
    private static final int DIMS = 100;

    private TensorFlowInferenceInterface mTfiInterface;


    private TextView mTxvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brain_demo);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTensorFlow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.BTN_Hello:
                getResults();
                break;

            default:
                break;
        }
    }

    private void initViews(){
        mTxvResults = findViewById(R.id.TXV_Hello);
        Button btnHello = findViewById(R.id.BTN_Hello);
        btnHello.setOnClickListener(this);
    }

    /**
    *   Initialization
    *     1. load so library
    *     2. initialize TensorFlowInferenceInterface
    **/
    private void initTensorFlow(){
        AssetManager assetManager = getAssets();

        System.loadLibrary("tensorflow_inference");
        try {
            FileInputStream fis = openFileInput("graphdef.pb");
            mTfiInterface = new TensorFlowInferenceInterface(fis);
        }catch (IOException e){
            Log.e(TAG, "open model failed");
            e.printStackTrace();
        }
    }

    /**
    *   1. Set input
    *   2. run tensor flow
    *   3. Get output
    **/
    private void getResults(){

        final String[] input_sequences = {"encoder0", "encoder1", "encoder2", "encoder3", "encoder4"};
        final byte[] input_bytes = {0, 4, 7, 57, 74};
        final long dims = 5;
        final String[] outputs = {"weights0", "weights1", "weights2", "weights3", "weights4"};

        //final String QUESTION =  "你是谁";
        byte[] an = new byte[32];

        if (mTfiInterface ==null){
            Log.e(TAG, "tensor flow inference interface is null");
            return;
        }

        // set input data
        //byte[] q = QUESTION.getBytes("UTF-8");
        //mTxvResults.append(QUESTION);
        for(int i=0; i<4; i++){
            mTfiInterface.feed(input_sequences[i], input_bytes, dims);
        }

        // run tensor flow
       // String[] outputNames = new String[]{outputs};
        mTfiInterface.run(outputs);

        // get output results
        for(int i=0; i<4; i++){
            mTfiInterface.fetch(outputs[i], an);
            Log.i(TAG, "output[" + i + "]: " + new String(an));
            mTxvResults.append(new String(an));
        }
    }
}
