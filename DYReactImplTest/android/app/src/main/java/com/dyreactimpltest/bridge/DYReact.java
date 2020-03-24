package com.dyreactimpltest.bridge;

import android.telecom.Call;

import androidx.annotation.NonNull;

import com.dynamicyield.dyapi.DYApi;
import com.dynamicyield.dyapi.DYApiResults.DYEvaluatorSet;
import com.dynamicyield.dyapi.DYApiResults.DYSetSecretResult;
import com.dynamicyield.dyapi.DYApiResults.DYTrackResult;
import com.dynamicyield.engine.DYCustomSettings;
import com.dynamicyield.engine.DYPageContext;
import com.dynamicyield.engine.DYRecommendationListenerItf;
import com.dynamicyield.state.DYExperimentsState;
import com.dynamicyield.userdata.external.DYUserData;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class DYReact extends ReactContextBaseJavaModule{
    private static ReactApplicationContext mReactContext;

    private boolean setSecretCallback = false;

    DYReact(ReactApplicationContext context) {
        super(context);
        mReactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "DYReact";
    }

    @ReactMethod
    public void setSecret(String secretKey,Callback callback) {
        DYApi.setContextAndSecret(mReactContext, secretKey, new DYSetSecretResult() {
            @Override
            public void onEnd(DYExperimentsState dyExperimentsState) {
                if (!setSecretCallback && dyExperimentsState.ordinal() >= 2) {
                    setSecretCallback = true;
                    callback.invoke(dyExperimentsState.ordinal());
                }
            }
        });
    }

    @ReactMethod
    public void pageView(String uniqueID, ReadableMap context, Callback callback) {
        DYPageContext dyPageContext = convertReadableMapToDYContext(context,callback);

        DYApi.getInstance().trackPageView(uniqueID, dyPageContext, new DYTrackResult() {
            @Override
            public void onEnd(String pageName) {
                callback.invoke(pageName);
            }
        });
    }

    @ReactMethod
    public void trackEvent(String eventName, ReadableMap params, Callback callback) {
        JSONObject jsonParams = (params != null) ? new JSONObject(params.toHashMap()) : null;

        DYApi.getInstance().trackEvent(eventName,jsonParams, new DYTrackResult() {
            @Override
            public void onEnd(String pageName) {
                callback.invoke(pageName);
            }
        });
    }

    @ReactMethod
    public void identifyUser(ReadableMap userData, Callback callback){
        if (userData != null) {
            DYUserData dyUserData = new DYUserData(new JSONObject(userData.toHashMap()));
            DYApi.getInstance().identifyUser(dyUserData);
        } else {
            callback.invoke("No user data supplied");
        }
    }

    @ReactMethod
    public void consentOptOut() {
        DYApi.getInstance().consentOptOut();
    }

    @ReactMethod
    public void consentOptIn() {
        DYApi.getInstance().consentOptIn();
    }

    @ReactMethod
    public void setEvaluator(String evaluatorID, ReadableArray evaluatorData, boolean saveBetweenSessions, Callback callback){
        JSONArray data = evaluatorData != null ? new JSONArray(evaluatorData.toArrayList()) : null;

        DYApi.getInstance().setEvaluator(evaluatorID, data, saveBetweenSessions, new DYEvaluatorSet() {
            @Override
            public void onEvaluatorSet(String evaluatorID, JSONArray evaluatorData, boolean saveBetweenSessions) {
                try {
                    callback.invoke(evaluatorID, convertJsonToArray(evaluatorData), saveBetweenSessions);
                } catch (JSONException e) {

                }
            }
        });
    }

    @ReactMethod
    public void getSmartVariable(String varName, String defaultValue, Callback callback){
        Object result = DYApi.getInstance().getSmartVariable(varName, defaultValue);

        callback.invoke(varName,result instanceof String ? (String)result : (Double)result);
    }

    @ReactMethod
    public void sendRecommendationRequest(String widgetID,ReadableMap context, Callback callback){
        DYPageContext dyPageContext = convertReadableMapToDYContext(context,callback);

        DYApi.getInstance().sendRecommendationsRequest(widgetID, dyPageContext, new DYRecommendationListenerItf() {
            @Override
            public void onRecommendationResult(JSONArray jsonArray, String widgetID) {
                try {
                    callback.invoke(convertJsonToArray(jsonArray),widgetID);
                } catch (JSONException e) {
                    callback.invoke(null,widgetID);
                }
            }
        });
    }

    @ReactMethod
    public void trackRecomItemClick(String widgetID, String itemId) {
        DYApi.getInstance().trackRecomItemClick(widgetID,itemId);
    }

    @ReactMethod
    public void trackRecomItemRealImpression(String widgetID, ReadableArray itemIds) {
        DYApi.getInstance().trackRecomItemRealImpression(widgetID,itemIds.toArrayList().toArray(new String[0]));
    }

    @ReactMethod
    public void setUseEuropeanServer(boolean on){
        DYCustomSettings settings = new DYCustomSettings();
        settings.useEuropeanServer(on);
        DYApi.setCustomSettings(settings);
    }

    private DYPageContext convertReadableMapToDYContext(ReadableMap context, Callback callback){
        DYPageContext dyPageContext = null;
        if (context != null) {
            dyPageContext = new DYPageContext(new JSONObject(context.toHashMap()));
        } else {
            callback.invoke("ERROR");
        }


        return dyPageContext;
    }

    private static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
        WritableMap map = new WritableNativeMap();

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                map.putMap(key, convertJsonToMap((JSONObject) value));
            } else if (value instanceof  JSONArray) {
                map.putArray(key, convertJsonToArray((JSONArray) value));
            } else if (value instanceof  Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof  Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof  Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof String)  {
                map.putString(key, (String) value);
            } else {
                map.putString(key, value.toString());
            }
        }
        return map;
    }

    private static WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
        WritableArray array = new WritableNativeArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                array.pushMap(convertJsonToMap((JSONObject) value));
            } else if (value instanceof  JSONArray) {
                array.pushArray(convertJsonToArray((JSONArray) value));
            } else if (value instanceof  Boolean) {
                array.pushBoolean((Boolean) value);
            } else if (value instanceof  Integer) {
                array.pushInt((Integer) value);
            } else if (value instanceof  Double) {
                array.pushDouble((Double) value);
            } else if (value instanceof String)  {
                array.pushString((String) value);
            } else {
                array.pushString(value.toString());
            }
        }
        return array;
    }
}
