package com.skeystoresdk;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * This class echoes a string called from JavaScript.
 */
public class SKeystoreSDK extends CordovaPlugin {

    private static final String LOG_TAG = "SamsungBlockchainSDK";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {

            Log.v(LOG_TAG, "SAMSUNG Blockchain Keystore SDK : execute " + args);

            String message = args.getString(0);
            Log.v(LOG_TAG, "SAMSUNG Blockchain Keystore SDK : message " + message);
            int index = args.getInt(1);
            Log.v(LOG_TAG, "SAMSUNG Blockchain Keystore SDK : index " + index);

            this.coolMethod(message,index,callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message,int index, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message+ Integer.toString(index));
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
