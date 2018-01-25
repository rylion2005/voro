package com.voro.speech.ear;

import android.util.Log;


import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
//import com.baidu.android.voicedemo.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  RecognitionEventListener
 *
 *  This class implements EventListener which notify register recognition event
 *  information. The register can process this event by overriding onEvent().
 *
 */

public class RecognitionEventListener implements EventListener {
    private static final String TAG = "RecognitionEvtListener";
    private IRecognitionCallback mCallback;

    public RecognitionEventListener(){
        mCallback = new RecognitionCallback();
    }

    public RecognitionEventListener(IRecognitionCallback callback) {
        mCallback = callback;
    }

    /*
    *  onEvent()
    *
    *  @param name
    *       This is a string name declared by SpeechConstant.CALLBACK_EVENT_XXX
    *  @param params
    *       This is a jason text transferred from network
    *  @param data
    *  @param offset
    *  @param length
    *
    */

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {

        RecognitionResult rr;

        Log.i(TAG, "--------------------------------------------------");
        Log.i(TAG, "name: " + name);
        Log.i(TAG, "params: " + params);
        Log.i(TAG, "data: " + data);
        Log.i(TAG, "offset: " + offset + "; length: " + length);
        Log.i(TAG, "--------------------------------------------------");

        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_LOADED)) {
            mCallback.onOfflineLoaded();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_UNLOADED)) {
            mCallback.onOfflineUnLoaded();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
            mCallback.onAsrReady();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
            mCallback.onAsrBegin();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
            mCallback.onAsrEnd();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            rr = RecognitionResult.parse(params);
            String[] results = rr.getRecognitionResults();
            if (rr.isFinalResult()) {
                mCallback.onAsrFinalResult(results, rr);
            } else if (rr.isPartialResult()) {
                mCallback.onAsrPartialResult(results, rr);
            } else if (rr.isNluResult()) {
                mCallback.onAsrOnlineNluResult(new String(data, offset, length));
            }
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
            rr = RecognitionResult.parse(params);
            if (rr.hasError()) {
                //int errorCode = rr.getError();
                //int subErrorCode = rr.getSubError();
                //mCallback.onAsrFinishError(errorCode, subErrorCode, ErrorTranslation.recogError(errorCode), recogResult.getDesc(), recogResult);
            } else {
                mCallback.onAsrFinish(rr);
            }
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH)) {
            mCallback.onAsrLongFinish();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
            mCallback.onAsrExit();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_VOLUME)) {
            //mCallback.onAsrVolume();
        } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_AUDIO)) {
            if (data.length != length) {
                // ......
            }
            mCallback.onAsrAudio(data, offset, length);
        }
    }
}
