package com.voro.speech.mouth;


import com.baidu.tts.client.TtsMode;
import java.util.Map;


/**
 *  This class is a data structure class
 */
public class TtsConfigurations {
    private static TtsConfigurations gInstance;

    private TtsMode mTtsMode = TtsMode.ONLINE;
    private Map<String, String> mParams;

    public static TtsConfigurations getInstance(){
        if (gInstance == null){
            gInstance = new TtsConfigurations();
        }
        return gInstance;
    }

    private TtsConfigurations() {
    }

    private TtsConfigurations(TtsMode ttsMode, Map<String, String> params) {
        mTtsMode = ttsMode;
        mParams = params;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public void setParams(Map<String, String> params){
        mParams = params;
    }

    public void addParam(String key, String value){
        mParams.put(key, value);
    }

    public void setTtsMode(TtsMode mode){
        mTtsMode = mode;
    }

    public TtsMode getTtsMode() {
        return mTtsMode;
    }

}
