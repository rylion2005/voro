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
import java.nio.FloatBuffer;

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

        final long NUMBER_OF_DIMENSIONS = 5;
        final String[] encoders = {"encoder0", "encoder1", "encoder2", "encoder3", "encoder4"};
        final String[] decoders = {"decoder0", "decoder1", "decoder2", "decoder3", "decoder4"};
        final String[] outputs = {
                "embedding_attention_seq2seq/embedding_attention_decoder/attention_decoder/AttnOutputProjection/BiasAdd",
                "embedding_attention_seq2seq/embedding_attention_decoder/attention_decoder/AttnOutputProjection_1/BiasAdd",
                "embedding_attention_seq2seq/embedding_attention_decoder/attention_decoder/AttnOutputProjection_2/BiasAdd",
                "embedding_attention_seq2seq/embedding_attention_decoder/attention_decoder/AttnOutputProjection_3/BiasAdd",
                "embedding_attention_seq2seq/embedding_attention_decoder/attention_decoder/AttnOutputProjection_4/BiasAdd"};

        final int[] encoderInts = {0, 0, 5, 7, 0};
        final int[] decoderInts = {1, 11, 13, 15, 2};

        if (mTfiInterface == null){
            Log.e(TAG, "tensor flow inference interface is null");
            return;
        }

        for(int i = 0; i < NUMBER_OF_DIMENSIONS; i++){
            mTfiInterface.feed(encoders[i], encoderInts, NUMBER_OF_DIMENSIONS);
            mTfiInterface.feed(decoders[i], decoderInts, NUMBER_OF_DIMENSIONS);
        }

        mTfiInterface.run(outputs);
        for(int i = 0; i < NUMBER_OF_DIMENSIONS; i++){
            FloatBuffer answers = FloatBuffer.allocate(512);
            mTfiInterface.fetch(outputs[i], answers);
            answers.get();
            Log.i(TAG, "output[" + i + "]: " + answers.get());
            mTxvResults.append("\n" + Float.toString(answers.get()));
        }
    }
}
