package com.voro.speech.ear;

import android.content.Context;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONObject;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

/**
 * Created by fujiayi on 2017/6/13.
 * EventManager内的方法如send 都可以在主线程中进行，SDK中做过处理
 */

public class MyRecognizer {
    private static final String TAG = "MyRecognizer";

    private static boolean isOfflineEngineLoaded = false;
    private static boolean isInited = false;

    private EventManager mEventManager;
    private EventListener mEventListener;
    private Map<String, Object> mParamMaps;

    /**
     * 初始化
     *
     * @param context
     */
    public MyRecognizer(Context context){
        this(context, new RecognitionCallback());
    }

    /**
     * 初始化
     *
     * @param context
     * @param callback 将EventListener结果做解析的DEMO回调。使用RecognitionListener 适配EventListener
     */
    public MyRecognizer(Context context, IRecognitionCallback callback) {
        this(context, new RecognitionEventListener(callback));
    }

    /**
     * 初始化 提供 EventManagerFactory需要的Context和EventListener
     *
     * @param context
     * @param listener
     */
    public MyRecognizer(Context context, EventListener listener) {
        if (isInited) {
            //Logger.error(TAG, "还未调用release()，请勿新建一个新类");
            throw new RuntimeException("还未调用release()，请勿新建一个新类");
        }
        isInited = true;
        mEventListener = listener;
        mEventManager = EventManagerFactory.create(context, "asr");
        mEventManager.registerListener(listener);

        init();
    }


    /**
     * @param params
     */
    public void loadOfflineEngine(Map<String, Object> params) {
        String json = new JSONObject(params).toString();
        //Logger.info(TAG + ".Debug", "loadOfflineEngine params:" + json);
        mEventManager.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, json, null, 0, 0);
        isOfflineEngineLoaded = true;
        // 没有ASR_KWS_LOAD_ENGINE这个回调表试失败，如缺少第一次联网时下载的正式授权文件。
    }

    public void start() {
        String json = new JSONObject(mParamMaps).toString();
        mEventManager.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    public void start(Map<String, Object> params) {
        String json = new JSONObject(params).toString();
        mEventManager.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    /**
     * 提前结束录音等待识别结果。
     */
    public void stop() {
        //Logger.info(TAG, "停止录音");
        mEventManager.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
    }

    /**
     * 取消本次识别，取消后将立即停止不会返回识别结果。
     * cancel 与stop的区别是 cancel在stop的基础上，完全停止整个识别流程，
     */
    public void cancel() {
        //Logger.info(TAG, "取消识别");
        if (mEventManager != null) {
            mEventManager.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        }
    }


    public void release() {
        if (mEventManager == null) {
            return;
        }
        cancel();
        if (isOfflineEngineLoaded) {
            mEventManager.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, null, null, 0, 0);
            isOfflineEngineLoaded = false;
        }
        mEventManager.unregisterListener(mEventListener);
        mEventManager = null;
        isInited = false;
    }

    /*
    *  Set default parameters
    */
    private void init(){
        mParamMaps = new LinkedHashMap<String, Object>();
        mParamMaps.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        // params.put(SpeechConstant.NLU, "enable");
        // params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        // params.put(SpeechConstant.IN_FILE, "res:///com/baidu/android/voicedemo/16k_test.pcm");
        // params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        // params.put(SpeechConstant.PROP ,20000);
        // params.put(SpeechConstant.PID, 1537); // 中文输入法模型，有逗号
    }
}
