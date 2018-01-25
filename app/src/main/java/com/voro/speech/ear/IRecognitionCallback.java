package com.voro.speech.ear;

/**
 * Created by fujiayi on 2017/6/14.
 */

public interface IRecognitionCallback {

    int STATUS_NONE = 2;
    int STATUS_READY = 3;
    int STATUS_SPEAKING = 4;
    int STATUS_RECOGNITION = 5;
    int STATUS_FINISHED = 6;
    int STATUS_STOPPED = 10;
    int STATUS_WAITING_READY = 8001;
    int WHAT_MESSAGE_STATUS = 9001;
    int STATUS_WAKEUP_SUCCESS = 7001;
    int STATUS_WAKEUP_EXIT = 7003;

    /**
     * ASR_START 输入事件调用后，引擎准备完毕
     */
    void onAsrReady();

    /**
     * onAsrReady后检查到用户开始说话
     */
    void onAsrBegin();

    /**
     * 检查到用户开始说话停止，或者ASR_STOP 输入事件调用后，
     */
    void onAsrEnd();

    /**
     * onAsrBegin 后 随着用户的说话，返回的临时结果
     *
     * @param results 可能返回多个结果，请取第一个结果
     * @param result 完整的结果
     */
    void onAsrPartialResult(String[] results, RecognitionResult result);

    /**
     * 最终的识别结果
     *
     * @param results 可能返回多个结果，请取第一个结果
     * @param result 完整的结果
     */
    void onAsrFinalResult(String[] results, RecognitionResult result);

    void onAsrFinish(RecognitionResult result);

    void onAsrFinishError(int errorCode, int subErrorCode, String errorMessage, String descMessage,
                          RecognitionResult result);

    /**
     * 长语音识别结束
     */
    void onAsrLongFinish();

    void onAsrVolume(int volumePercent, int volume);

    void onAsrAudio(byte[] data, int offset, int length);

    void onAsrExit();

    void onAsrOnlineNluResult(String nluResult);

    void onOfflineLoaded();

    void onOfflineUnLoaded();
}
