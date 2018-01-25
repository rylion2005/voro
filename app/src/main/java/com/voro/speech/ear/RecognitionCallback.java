package com.voro.speech.ear;

/**
 *  This class is default implementation of IRecognitionCallback
 */

import android.util.Log;

public class RecognitionCallback implements IRecognitionCallback{
    private static final String TAG = "RecognitionCallback";

    // ASR state machine
    protected int mAsrStatus = STATUS_NONE;

    @Override
    public void onAsrReady() {
        mAsrStatus = STATUS_READY;
    }

    @Override
    public void onAsrBegin() {
        mAsrStatus = STATUS_SPEAKING;
    }

    @Override
    public void onAsrEnd() {
        mAsrStatus = STATUS_RECOGNITION;
    }


    @Override
    public void onAsrPartialResult(String[] results, RecognitionResult result) {

    }

    @Override
    public void onAsrFinalResult(String[] results, RecognitionResult result) {
        mAsrStatus = STATUS_FINISHED;
    }

    @Override
    public void onAsrFinish(RecognitionResult result) {
        mAsrStatus = STATUS_FINISHED;
    }


    @Override
    public void onAsrFinishError(int errorCode, int subErrorCode,
        String errorMessage, String descMessage, RecognitionResult result) {
        mAsrStatus = STATUS_FINISHED;
    }

    @Override
    public void onAsrLongFinish() {
        mAsrStatus = STATUS_FINISHED;
    }

    @Override
    public void onAsrVolume(int volumePercent, int volume) {

    }

    @Override
    public void onAsrAudio(byte[] data, int offset, int length) {
        if (offset != 0 || data.length != length) {
            byte[] actualData = new byte[length];
            System.arraycopy(data, 0, actualData, 0, length);
            data = actualData;
        }

        Log.i(TAG, "音频数据回调, length:" + data.length);
    }

    @Override
    public void onAsrExit() {
        mAsrStatus = STATUS_NONE;
    }

    @Override
    public void onAsrOnlineNluResult(String nluResult) {
        mAsrStatus = STATUS_FINISHED;
    }

    @Override
    public void onOfflineLoaded() {

    }

    @Override
    public void onOfflineUnLoaded() {

    }
}
