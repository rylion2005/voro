package com.voro.speech.ear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
*  This class is a data structure class which is used to define
*    recognition result. data source is encoded by json
*
*  Recognition result json template
*
** **********************************************************************
{
	"results_recognition":["今天天气很"],
	"origin_result":{
		"corpus_no":6510736084238725443,
		"err_no":0,
		"result":{
			"word":["今天天气很"]
		},
		"sn":"d567204c-db0e-4e6e-98f1-7e3459232dce"
	},
	"error":0,
	"best_result":"今天天气很",
	"result_type":"partial_result"
}

{
	"results_recognition":["今天天气很好"],
	"origin_result":{
		"corpus_no":6510736084238725443,
		"err_no":0,
		"result":{
			"word":["今天天气很好","金天天气很好"]
		},
		"sn":"d567204c-db0e-4e6e-98f1-7e3459232dce",
		"voice_energy":18649.296875
	},
	"error":0,
	"best_result":"今天天气很好",
	"result_type":"final_result"
}
** **********************************************************************
*/

public class RecognitionResult {

    public static final String RESULT_ERROR = "error";
    public static final String RESULT_SUBERROR = "sub_error";
    public static final String RESULT_DESCRIPTION = "desc";
    public static final String RESULT_TYPE = "result_type";
    public static final String RESULT_ORIGIN_RESULT = "origin_result";
    public static final String RESULT_RECOGNITION= "results_recognition";
    public static final String RESULT_BEST_RESULT = "best_result";

    public static final String RESULT_TYPE_FINAL_RESULT = "final_result";
    public static final String RESULT_TYPE_PARTIAL_RESULT = "partial_result";
    public static final String RESULT_TYPE_NLU_RESULT = "nlu_result";

    public static final int ERROR_NONE = 0;

	public static final String[] RECOGNITION_RESULT_KEYS = {
            RESULT_ERROR, RESULT_SUBERROR, RESULT_DESCRIPTION,
            RESULT_TYPE, RESULT_ORIGIN_RESULT, RESULT_RECOGNITION,
            RESULT_BEST_RESULT
    };

    private String mOriginalJsonText;
    private String[] mRecognitionResults;
    private String mOriginalResult;
    private String mSn;
    private String mDescription;
    private String mResultType;
    private String mBestResult;
    private int mError = -1;
    private int mSubError = -1;

    public RecognitionResult(){}

    public  static RecognitionResult parse(String jsonText) {

        if ((jsonText == null) || (jsonText.length() == 0)){
            return null;
        }

        RecognitionResult rr = new RecognitionResult();
        rr.setOriginalJson(jsonText);
        try {
            JSONObject json = new JSONObject(jsonText);
            int error = json.optInt(RESULT_ERROR);
            //int subError = json.optInt(RESULT_SUBERROR);
            rr.setError(error);
            rr.setDescription(json.optString(RESULT_DESCRIPTION));
            rr.setResultType(json.optString(RESULT_TYPE));
            rr.setBestResult(json.optString(RESULT_BEST_RESULT));
            if (error == ERROR_NONE) {
                rr.setOriginalResult(json.getString(RESULT_ORIGIN_RESULT));
                JSONArray arr = json.optJSONArray(RESULT_RECOGNITION);
                if (arr != null) {
                    int size = arr.length();
                    String[] results = new String[size];
                    for (int i = 0; i < size; i++) {
                        results[i] = arr.getString(i);
                    }
                    rr.setRecognitionResults(results);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            rr = null;
        }
        return rr;
    }

    public static String getRecognitionFinalBestResult(String jsonText){
        String result = null;
        if ((jsonText == null) || (jsonText.length() == 0)){
            return null;
        }

        RecognitionResult rr = parse(jsonText);
        if ((rr != null) && rr.isFinalResult()){
            result = rr.getBestResult();
        }

        return result;
    }

    public boolean hasError() {
        return mError != ERROR_NONE;
    }

    public boolean isFinalResult() {
        return RESULT_TYPE_FINAL_RESULT.equals(mResultType);
    }

    public boolean isPartialResult() {
        return RESULT_TYPE_PARTIAL_RESULT.equals(mResultType);
    }

    public boolean isNluResult() {
        return RESULT_TYPE_NLU_RESULT.equals(mResultType);
    }

    public String getOriginalJson() {
        return mOriginalJsonText;
    }

    public void setOriginalJson(String text) {
        mOriginalJsonText = text;
    }

    public String[] getRecognitionResults() {
        return mRecognitionResults;
    }

    public void setRecognitionResults(String[] results) {
        mRecognitionResults = results;
    }

    public String getSn() {
        return mSn;
    }

    public void setSn(String sn) {
        mSn = sn;
    }

    public int getError() {
        return mError;
    }

    public void setError(int error) {
        mError = error;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getOriginalResult() {
        return mOriginalResult;
    }

    public void setOriginalResult(String result) {
        mOriginalResult = result;
    }

    public String getResultType() {
        return mResultType;
    }

    public void setResultType(String type) {
        mResultType = type;
    }

    public void setSubError(int error){
        mSubError = error;
    }

    public int getSubError() {
        return mSubError;
    }

    public String getBestResult(){
        return mBestResult;
    }

    public void setBestResult(String result){
        mBestResult = result;
    }

    private static boolean isEmpty(String string){
        boolean empty = false;
        if ((string == null) || (string.length() == 0)){
            empty = true;
        }

        return empty;
    }
}
