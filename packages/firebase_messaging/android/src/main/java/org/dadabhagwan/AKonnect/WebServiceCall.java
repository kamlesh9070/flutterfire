package org.dadabhagwan.AKonnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.dbo.DBHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by coder2 on 25-Jun-2018.
 */

public class WebServiceCall {

  protected static final String TAG = "AKonnect[WSCall]";

  private static String getLatestMessagesByMsgIdApiUrl(Context context) {
    SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
    return sharedPreferencesTask.getString(SharedPrefConstants.LATEST_MESSAGES_BY_MSGID_APIURL);
  }

  private static String getApiUrl(Context context) {
        /*SharedPreferences sharedPreferences =
                ApplicationUtility.getAkonnectSharedPreferences(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
        return sharedPreferences.getString(SharedPrefConstants.API_URL,null);*/
    SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
    return sharedPreferencesTask.getString(SharedPrefConstants.API_URL);
  }

  public static void sendNotificationLog(Context context, String pushType, String msgId) {

    try {
      //SharedPreferences sharedPreferences = ApplicationUtility.getAkonnectSharedPreferences(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      // if (sharedPreferences != null) {
                /*Map<String, ?> allEntries = sharedPreferences.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Log.d(TAG, "NOTIFICATION_LOG_PREF Shared values" + entry.getKey() + ": " + entry.getValue().toString());
                }*/
      //String ApiUrl = sharedPreferences.getString(String.valueOf("ApiUrl"), null);
      String ApiUrl = getApiUrl(context);
      int ActivationSrNo = 0;

      if (ApiUrl != null && ApiUrl.trim() != "") {
        SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
        String ApiKey = sharedPreferencesTask.getString(SharedPrefConstants.API_KEY);
        String DeviceModel = sharedPreferencesTask.getString(SharedPrefConstants.DEVICE_MODEL);
        String DeviceOsVersion = sharedPreferencesTask.getString(SharedPrefConstants.DEVICE_OS_VERSION);
        String AppVersion = sharedPreferencesTask.getString(SharedPrefConstants.APP_VERSION);
        String NetworkState = NetworkUtils.getNetworkState(context);
        try {
          ActivationSrNo = Integer.parseInt(sharedPreferencesTask.getString(SharedPrefConstants.ACTIVATION_SRNO));
        } catch (Exception e) {
          e.printStackTrace();
        }
        ;

        Map<String, String> postData = new HashMap<String, String>();
        //postData.put("ApiKey", ApiKey);
        postData.put("ActivationSrNo", "" + ActivationSrNo);
        postData.put("DeviceModel", DeviceModel);
        postData.put("OsVersion", DeviceOsVersion);
        postData.put("AppVersion", AppVersion);
        postData.put("NetworkState", NetworkState);
        postData.put("PushType", pushType);
        postData.put("MsgId", msgId);
        postData.put("NotificationReceivedTime", new Date().toString());

        for (Map.Entry<String, ?> entry : postData.entrySet()) {
          Log.d(TAG, "sendNotificationLog postData values" + entry.getKey() + ": " + entry.getValue().toString());
        }

        HttpPostAsyncTask task = new HttpPostAsyncTask(postData, ApiKey);
        task.execute(ApiUrl);
      }
      //}
    } catch (Exception e) {
      Log.d(TAG, "sendNotificationLog Exception : " + e.getMessage());
    }

  }

  public static void fetchMsgFromServer(Context context, AsyncResponseListner asyncResponseListner) {
    try {
      int lastMsgIdFromInbox = 0;
      int maxMsgId = 0;
      int lastNotificationId = 0;
      int ActivationSrNo = 0;

      String latestMessagesByMsgIdApiUrl = getLatestMessagesByMsgIdApiUrl(context);
      if (latestMessagesByMsgIdApiUrl != null && latestMessagesByMsgIdApiUrl.trim() != "") {
        SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
        lastMsgIdFromInbox = sharedPreferencesTask.getInt(SharedPrefConstants.LAST_MSGID_FROM_INBOX);
        lastNotificationId = sharedPreferencesTask.getInt(SharedPrefConstants.LAST_MSGID_FROM_NOTIFICATION);
        maxMsgId = (lastNotificationId >= lastMsgIdFromInbox) ? lastNotificationId : lastMsgIdFromInbox;

        int SubscriberId = sharedPreferencesTask.getInt(SharedPrefConstants.SUBSCRIBER_ID);
        try {
          ActivationSrNo = Integer.parseInt(sharedPreferencesTask.getString(SharedPrefConstants.ACTIVATION_SRNO));
        } catch (Exception e) {
          e.printStackTrace();
        }
        ;

        //For testing only
        //maxMsgId=10645;

        if (SubscriberId > 0 && ActivationSrNo > 0) {
          Map<String, String> postData = new HashMap<String, String>();
          postData.put("ActivationSrNo", "" + ActivationSrNo);
          postData.put("SubscriberId", "" + SubscriberId);
          postData.put("MsgId", "" + maxMsgId);

          Log.d(TAG, "fetchMsgFromServer :: lastMsgIdFromInbox : " + lastMsgIdFromInbox + " , lastNotificationId : " + lastNotificationId
            + " , latestMessagesByMsgIdApiUrl : " + latestMessagesByMsgIdApiUrl);

          for (Map.Entry<String, ?> entry : postData.entrySet()) {
            Log.d(TAG, "fetchMsgFromServer postData values" + entry.getKey() + ": " + entry.getValue().toString());
          }

          HttpPostAsyncTask task = new HttpPostAsyncTask(postData, "", asyncResponseListner);
          task.execute(latestMessagesByMsgIdApiUrl);
        }
      }
    } catch (Exception e) {
      Log.d(TAG, "sendNotificationLog Exception : " + e.getMessage());
      e.getStackTrace();
      e.printStackTrace();
    }

  }


}
