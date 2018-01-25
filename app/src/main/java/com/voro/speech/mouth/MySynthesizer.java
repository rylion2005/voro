package com.voro.speech.mouth;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.voro.speech.utils.AuthKeys;


/**
 * This class encapsulate SpeechSynthesizer function for client
 */

public class MySynthesizer {
    private static final String TAG = "MySynthesizer";

    /*
    *  It is MUST only when ttsMode = TtsMode.MIX
    *
    *  YOU MUST copy asserts/*.dat to TEMP_DIR
    *    bd_etts_speech_male.dat: male voice
    *    bd_etts_speech_female.dat: female voice
    */
    private static final String TEMP_DIR = "/sdcard/baiduTTS";
    private static final String TEXT_FILENAME = TEMP_DIR + "/" + "bd_etts_text.dat";
    private static final String MODEL_FILENAME = TEMP_DIR + "/" + "bd_etts_speech_male.dat";

    public static final int INIT_SUCCESS = 0;

    // Handler message header
    public static final int MSG_PRINT = 0xB0B;
    public static final int MSG_CHANGE_INPUT_TEXT_SELECTION = 0xB0C;
    public static final int MSG_CHANGE_SYNTHES_TEXT_SELECTION = 0xB0D;

    private static boolean isInitied = false;

    private Context mContext;
    private Handler mHandler;
    private AuthKeys mAuthKeys;
    private TtsMode mTtsMode = TtsMode.ONLINE;
    private SpeechSynthesizer mSpeechSynthesizer;
    private SpeechSynthesizerListener mListener;
    private boolean isCheckFile = true;


    public MySynthesizer(Context context, Handler handler) {
        if (isInitied) {
            // SpeechSynthesizer.getInstance() 不要连续调用
            throw new RuntimeException("MySynthesizer 类里面 SpeechSynthesizer还未释放，请勿新建一个新类");
        }
        mContext = context;
        mHandler = handler;
        isInitied = true;
        mListener = new SynthesisListener();
        initTts();
    }

    /**
     * 注意该方法需要在新线程中调用。且该线程不能结束。详细请参见NonBlockSyntherizer的实现
     * @return
     */
    protected boolean initTts() {

        mAuthKeys = AuthKeys.getInstance(mContext);

        sendMessage("初始化开始");
        boolean isMix = mTtsMode.equals(TtsMode.MIX);
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(mContext);
        mSpeechSynthesizer.setSpeechSynthesizerListener(mListener);
        mSpeechSynthesizer.setAppId(mAuthKeys.getAppId());
        mSpeechSynthesizer.setApiKey(mAuthKeys.getApiKey(), mAuthKeys.getSecretKey());

        if (isMix) {
            // 授权检测接口(只是通过AuthInfo进行检验授权是否成功。选择纯在线可以不必调用auth方法。
            AuthInfo authInfo = mSpeechSynthesizer.auth(mTtsMode);
            if (!authInfo.isSuccess()) {
                // 离线授权需要网站上的应用填写包名。本demo的包名是com.baidu.tts.sample，定义在build.gradle中
                String errorMsg = authInfo.getTtsError().getDetailMessage();
                sendMessage("鉴权失败 =" + errorMsg);
                return false;
            } else {
                sendMessage("验证通过，离线正式授权文件存在。");
            }
        }

        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "4");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);

        Map<String, String> params = new HashMap<>();
        if (isMix) {
            params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
        }

        TtsConfigurations configs = TtsConfigurations.getInstance();
        configs.setParams(params);
        //AutoCheck.getInstance(mContext.getApplicationContext()).check(configs, mHandler);

        // 初始化tts
        int result = mSpeechSynthesizer.initTts(mTtsMode);
        if (result != 0) {
            sendMessage("【error】initTts 初始化失败 + errorCode：" + result);
            return false;
        }
        // 此时可以调用 speak和synthesize方法
        sendMessage(INIT_SUCCESS, "合成引擎初始化成功");
        return true;
    }

    /**
     * 合成并播放
     *
     * @param text 小于1024 GBK字节，即512个汉字或者字母数字
     * @return
     */
    public int speak(String text) {
        Log.i(TAG, "speak text:" + text);
        return mSpeechSynthesizer.speak(text);
    }

    /**
     * 合成并播放
     *
     * @param text        小于1024 GBK字节，即512个汉字或者字母数字
     * @param utteranceId 用于listener的回调，默认"0"
     * @return
     */
    public int speak(String text, String utteranceId) {
        return mSpeechSynthesizer.speak(text, utteranceId);
    }

    /**
     * 只合成不播放
     *
     * @param text
     * @return
     */
    public int synthesize(String text) {
        return mSpeechSynthesizer.synthesize(text);
    }

    public int synthesize(String text, String utteranceId) {
        return mSpeechSynthesizer.synthesize(text, utteranceId);
    }

    public int batchSpeak(List<Pair<String, String>> texts) {
        List<SpeechSynthesizeBag> bags = new ArrayList<SpeechSynthesizeBag>();
        for (Pair<String, String> pair : texts) {
            SpeechSynthesizeBag speechSynthesizeBag = new SpeechSynthesizeBag();
            speechSynthesizeBag.setText(pair.first);
            if (pair.second != null) {
                speechSynthesizeBag.setUtteranceId(pair.second);
            }
            bags.add(speechSynthesizeBag);

        }
        return mSpeechSynthesizer.batchSpeak(bags);
    }

    public void setParams(Map<String, String> params) {
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                mSpeechSynthesizer.setParam(e.getKey(), e.getValue());
            }
        }
    }

    public int pause() {
        return mSpeechSynthesizer.pause();
    }

    public int resume() {
        return mSpeechSynthesizer.resume();
    }

    public int stop() {
        return mSpeechSynthesizer.stop();
    }

    /**
     * 引擎在合成时该方法不能调用！！！
     * 注意 只有 TtsMode.MIX 才可以切换离线发音
     *
     * @return
     */
    public int loadModel(String modelFilename, String textFilename) {
        int res  = mSpeechSynthesizer.loadModel(modelFilename, textFilename);
        sendMessage("切换离线发音人成功。");
        return res;
    }

    /**
     * 设置播放音量，默认已经是最大声音
     * 0.0f为最小音量，1.0f为最大音量
     *
     * @param leftVolume  [0-1] 默认1.0f
     * @param rightVolume [0-1] 默认1.0f
     */
    public void setStereoVolume(float leftVolume, float rightVolume) {
        mSpeechSynthesizer.setStereoVolume(leftVolume, rightVolume);
    }

    public void release() {
        mSpeechSynthesizer.stop();
        mSpeechSynthesizer.release();
        mSpeechSynthesizer = null;
        isInitied = false;
    }

    protected void sendMessage(String message) {
        sendMessage(MSG_PRINT, message);
    }

    protected void sendMessage(int action, String message) {
        Log.i(TAG, "sendMessage: " + message);
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = action;
            msg.obj = message;
            mHandler.sendMessage(msg);
        }
    }
}
