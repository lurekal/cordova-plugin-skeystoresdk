package com.msline.samsungblockchainsdk;

import com.samsung.android.sdk.coldwallet.*;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.List;

public class SamsungBlockchainSDK extends CordovaPlugin {

    private ScwService scw = null;
    private static final String LOG_TAG = "SamsungBlockchainSDK";
    private Activity currentActivity;

    private final static int DEEPLINK_MAIN = 1;
    private final static int DEEPLINK_CHANGE_PIN = 2;
    private final static int DEEPLINK_RESET = 3;
    private final static int DEEPLINK_DISPLAY_WALLET = 4;
    private final static int DEEPLINK_NOTICE_CONTENT = 5;
    private final static int DEEPLINK_GALAXY_STORE = 6;
    private final static int DEEPLINK_BACKUP_WALLET = 7;//추가

    private final static int SUCCESS = 1;
    private final static int FAIL = 0;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.v(LOG_TAG, "SAMSUNG Blockchain Keystore SDK : initialize");
        super.initialize(cordova, webView);
        currentActivity = this.cordova.getActivity();
        try {
            this.scw = ScwService.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.v(LOG_TAG, "SAMSUNG Blockchain Keystore SDK : execute " + action);

        cordova.setActivityResultCallback(this);

        if (action.equals("getInstance")) {
            this.getInstance(callbackContext);
            return true;
        } else if (action.equals("getKeystoreApiLevel")) {
            this.getKeystoreApiLevel(callbackContext);
            return true;
        } else if (action.equals("checkMandatoryAppUpdate")) {
            this.checkMandatoryAppUpdate(callbackContext);
            return true;
        } else if (action.equals("getSeedHash")) {
            this.getSeedHash(callbackContext);
            return true;
        } else if (action.equals("getAddress")) {
            int index = args.getInt(0);
            this.getAddress(index, callbackContext);
            return true;
        } else if (action.equals("getPublicKey")) {
            int index = args.getInt(0);
            this.getPublicKey(index, callbackContext);
            return true;
        } else if (action.equals("signTransaction")) {
            this.signTransaction(args, callbackContext);
            return true;
        } else if (action.equals("signPersonalMessage")) {
            this.signPersonalMessage(args, callbackContext);
            return true;
        } else if (action.equals("linkSDK")) {
            int urlcode = args.getInt(0);
            this.linkSDK(urlcode, callbackContext);
            return true;
        }

        return false;

    }

    //동일
    private void getInstance(CallbackContext callbackContext) {
        try {
            if (this.scw != null) callbackContext.success(SUCCESS);
            else callbackContext.success(FAIL);
        } catch (Exception e) {
            callbackContext.error("getInstance error: " + e.toString());
        }
    }

    //동일
    private void getKeystoreApiLevel(CallbackContext callbackContext) {
        try {
            int level = this.scw.getKeystoreApiLevel();
            callbackContext.success(level);
        } catch (Exception e) {
            callbackContext.error("getKeystoreApiLevel error: " + e.toString());
        }
    }

    //추가
    private void isRootSeedBackedUp(CallbackContext callbackContext) {
        try {
            boolean isrootseedbackup = this.scw.isRootSeedBackedUp();
            callbackContext.success(isrootseedbackup);
        } catch (Exception e) {
            callbackContext.error("isRootSeedBackedUp error: " + e.toString());
        }
    }

    //동일
    private void checkMandatoryAppUpdate(CallbackContext callbackContext) {
        try {
            ScwService.ScwCheckForMandatoryAppUpdateCallback callback = new ScwService.ScwCheckForMandatoryAppUpdateCallback() {
                @Override
                public void onMandatoryAppUpdateNeeded(boolean needed) {
                    if (needed){
                        callbackContext.success(FAIL);
                        currentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ScwDeepLink.GALAXY_STORE)));
                    } else callbackContext.success(SUCCESS);
                }
            };

            ScwService.getInstance().checkForMandatoryAppUpdate(callback);
        } catch (Exception e) {
            callbackContext.error("checkMandatoryAppUpdate error: " + e.toString());
        }
    }

    //동일
    private void getSeedHash(CallbackContext callbackContext) {
        try {
            String seedHash = this.scw.getSeedHash();
            callbackContext.success(seedHash);
        } catch (Exception e) {
            callbackContext.error("getSeedHash error: " + e.toString());
        }
    }

    //동일
    private void getAddress(int index, CallbackContext callbackContext) {
        try {
            ScwService.ScwGetAddressListCallback callback = new ScwService.ScwGetAddressListCallback() {
                @Override
                public void onSuccess(List<String> addressList) {
                    try {
                        JSONArray jsonArray = new JSONArray();

                        for (String s : addressList) jsonArray.put(s);

                        callbackContext.success(jsonArray.getString(0));
                    } catch (JSONException e) {
                        callbackContext.error("getAddress error: " + e.toString());
                    }
                }

                @Override
                public void onFailure(int errorCode) {}
            };

            String hdpath = "m/44'/60'/0'/0/" + index;

            ArrayList<String> hdPathList = new ArrayList<>();
            hdPathList.add(hdpath);

            ScwService.getInstance().getAddressList(callback, hdPathList);

        } catch (Exception e) {
            callbackContext.error("getAddressList error: " + e.toString());
        }
    }

    //동일
    private void getPublicKey(int index, CallbackContext callbackContext) {
        try {
            ScwService.ScwGetExtendedPublicKeyListCallback callback = new ScwService.ScwGetExtendedPublicKeyListCallback() {
                @Override
                public void onSuccess(List<byte[]> extendedPublicKeyList) {
                    try {
                        JSONArray jsonArray = new JSONArray();

                        for (byte[] b : extendedPublicKeyList) jsonArray.put(toHexString(b, true));

                        callbackContext.success(jsonArray.getString(0));
                    } catch (JSONException e) {
                        callbackContext.error("getPublicKey error: " + e.toString());
                    }
                }

                @Override
                public void onFailure(int errorCode) {}
            };

            String hdpath = "m/44'/60'/0'/0/" + index;

            ArrayList<String> hdPathList = new ArrayList<>();
            hdPathList.add(hdpath);

            ScwService.getInstance().getExtendedPublicKeyList(callback, hdPathList);

        } catch (Exception e) {
            callbackContext.error("getPublicKey error: " + e.toString());
        }
    }

    //동일
    private void signTransaction(JSONArray args, CallbackContext callbackContext) {
        try {
            ScwService.ScwSignEthTransactionCallback callback = new ScwService.ScwSignEthTransactionCallback() {
                @Override
                public void onSuccess(byte[] signedEthTransaction) {
                    callbackContext.success(toHexString(signedEthTransaction, true));
                }

                @Override
                public void onFailure(int errorCode) {
                    callbackContext.error("signTransaction error: " + errorCode);
                }
            };

            int index = args.getInt(1);

            String hdPath = "m/44'/60'/0'/0/" + index;

            byte[] encodedUnsignedEthTx = encodedUnsignedEthTx(args);

            this.scw.signEthTransaction(callback, encodedUnsignedEthTx, hdPath);

        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.error("signTransaction error: " + e.toString());
        }
    }

    //동일
    private void signPersonalMessage(JSONArray args, CallbackContext callbackContext) {
        try {
            int index = args.getInt(1);

            ScwService.ScwSignEthPersonalMessageCallback callback = new ScwService.ScwSignEthPersonalMessageCallback() {
                @Override
                public void onSuccess(byte[] signedPersonalMessage) {
                    getAddress(index, callbackContext);
                }

                @Override
                public void onFailure(int errorCode) {
                    callbackContext.error("signPersonalMessage error: " + errorCode);
                }
            };

            byte[] unSignedMsg = args.getString(0).getBytes();

            String hdPath = "m/44'/60'/0'/0/" + index;

            ScwService.getInstance().signEthPersonalMessage(callback, unSignedMsg, hdPath);

        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.error("signPersonalMessage error: " + e.toString());
        }
    }

    //자체기능 추정
    private byte[] encodedUnsignedEthTx(JSONArray args) {
        try {
            JSONObject rawTransaction = args.getJSONObject(0);

            String to = rawTransaction.getString("to");

            String _nonce = rawTransaction.getString("nonce");
            String _gasPrice = rawTransaction.getString("gasPrice");
            String _gasLimit = rawTransaction.getString("gasLimit");
            String _value = rawTransaction.getString("value");
            String data = rawTransaction.getString("data");

            BigInteger nonce = new BigInteger(_nonce);
            double ethValue = Double.valueOf(_value);
            BigDecimal weiValue = BigDecimal.valueOf(ethValue).multiply(BigDecimal.valueOf(1, -18));
            BigInteger gasPrice = new BigInteger(_gasPrice);
            BigInteger gasLimit = new BigInteger(_gasLimit);

            RawTransaction rawTrx = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, weiValue.toBigInteger(), data);

            return TransactionEncoder.encode(rawTrx);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    //수정
    private void linkSDK(int urlcode, CallbackContext callbackContext) {
        try {

            String link = "";

            switch (urlcode) {
                case DEEPLINK_MAIN :
                    link = ScwDeepLink.MAIN;
                    break;
                case DEEPLINK_CHANGE_PIN :
                    link = ScwDeepLink.CHANGE_PIN;
                    break;
                case DEEPLINK_RESET :
                    link = ScwDeepLink.RESET;
                    break;
                case DEEPLINK_DISPLAY_WALLET :
                    link = ScwDeepLink.DISPLAY_WALLET;
                    break;
                case DEEPLINK_NOTICE_CONTENT :
                    link = ScwDeepLink.NOTICE_CONTENT;
                    break;
                case DEEPLINK_GALAXY_STORE :
                    link = ScwDeepLink.GALAXY_STORE;
                    break;
//추가
                case DEEPLINK_BACKUP_WALLET :
                    link = ScwDeepLink.BACKUP_WALLET;
                    break;
                default :
                    callbackContext.error("linkSDK error: wrong code");
                    return;
            }

            this.moveLink(link);
            callbackContext.success();

        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.error("linkSDK error: " + e.toString());
        }
    }

    //자체기능 추정
    private void moveLink(String link) {
        Uri uri = Uri.parse(link);
        Intent displayIntent = new Intent(Intent.ACTION_VIEW, uri);
        displayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        currentActivity.startActivity(intent);
    }

    //자체기능 추정
    private static String toHexString(byte[] input, boolean withPrefix) {
        StringBuilder stringBuilder = new StringBuilder();
        if (withPrefix) {
            stringBuilder.append("0x");
        }
        for (int i = 0; i < input.length; i++) {
            stringBuilder.append(String.format("%02x", input[i] & 0xFF));
        }

        return stringBuilder.toString();
    }
}
