package org.dadabhagwan.AKonnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.dbo.DBHelper;
import org.dadabhagwan.AKonnect.dto.DeviceDetail;
import org.dadabhagwan.AKonnect.dto.UserProfile;

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
        DeviceDetail deviceDetail = SharedPreferencesTask.getDeviceDetail(context);
        String DeviceModel = deviceDetail.getDeviceModel();
        String DeviceOsVersion = deviceDetail.getDeviceOs();
        String AppVersion = deviceDetail.getAppVersion();
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
      long maxMsgId = 0;
      long lastNotificationId = 0;
      int ActivationSrNo = 0;

      String latestMessagesByMsgIdApiUrl = getLatestMessagesByMsgIdApiUrl(context);
      if (latestMessagesByMsgIdApiUrl != null && latestMessagesByMsgIdApiUrl.trim() != "") {
        SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);

        lastNotificationId = sharedPreferencesTask.getInt(SharedPrefConstants.LAST_MSGID_FROM_NOTIFICATION);
        lastMsgIdFromInbox = sharedPreferencesTask.getInt(SharedPrefConstants.LAST_MSGID_FROM_INBOX);
        maxMsgId = (lastNotificationId >= lastMsgIdFromInbox) ? lastNotificationId : lastMsgIdFromInbox;

        UserProfile userProfile = SharedPreferencesTask.getUserProfile(context);
        String subscriberId = userProfile.getSubscriber();
        try {
          ActivationSrNo = Integer.parseInt(sharedPreferencesTask.getString(SharedPrefConstants.ACTIVATION_SRNO));
        } catch (Exception e) {
          e.printStackTrace();
        }
        ;

        //For testing only
        //maxMsgId=10645;

        if (subscriberId != null && ActivationSrNo > 0) {
          Map<String, String> postData = new HashMap<String, String>();
          postData.put("ActivationSrNo", "" + ActivationSrNo);
          postData.put("SubscriberId", "" + subscriberId);
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
