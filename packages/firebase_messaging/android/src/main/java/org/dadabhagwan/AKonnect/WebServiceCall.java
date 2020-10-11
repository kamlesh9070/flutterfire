package org.dadabhagwan.AKonnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.constants.WSConstant;
import org.dadabhagwan.AKonnect.dbo.AKDBHelper;
import org.dadabhagwan.AKonnect.dbo.DBHelper;
import org.dadabhagwan.AKonnect.dto.DeviceDetail;
import org.dadabhagwan.AKonnect.dto.NotificationLog;
import org.dadabhagwan.AKonnect.dto.PullNotificationDTO;
import org.dadabhagwan.AKonnect.dto.UserProfile;
import org.dadabhagwan.AKonnect.dto.UserRegData;

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
    ApplicationUtility.sendLog(context, "Notification:" + pushType);
/*    try {
      //SharedPreferences sharedPreferences = ApplicationUtility.getAkonnectSharedPreferences(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      // if (sharedPreferences != null) {
                *//*Map<String, ?> allEntries = sharedPreferences.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Log.d(TAG, "NOTIFICATION_LOG_PREF Shared values" + entry.getKey() + ": " + entry.getValue().toString());
                }*//*
      //String ApiUrl = sharedPreferences.getString(String.valueOf("ApiUrl"), null);
      String ApiUrl = WSConstant.logUrl;
      int ActivationSrNo = 0;

      NotificationLog notificationLog = new NotificationLog();
      notificationLog.setFieldsFromDTO(SharedPreferencesTask.getUserRegData(context));
      notificationLog.setFieldsFromDTO(SharedPreferencesTask.getDeviceDetail(context));
      //notificationLog.setTimestamp(new Date().getTime());
      HttpPostAsyncTask task = new HttpPostAsyncTask(new Gson().toJson(notificationLog), null);
      task.execute(ApiUrl);
      //}
    } catch (Exception e) {
      Log.d(TAG, "sendNotificationLog Exception : " + e.getMessage());
    }*/

  }

  public static void fetchMsgFromServer(Context context, AsyncResponseListner asyncResponseListner) {
    Log.d(TAG, "Inside fetchMsgFromServer");
    try {
      int lastMsgIdFromInbox = 0;
      int maxMsgId = 0;
      int lastNotificationId = 0;
      PullNotificationDTO pullNotificationDTO = new PullNotificationDTO();
      SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      lastNotificationId = sharedPreferencesTask.getInt(SharedPrefConstants.LAST_MSGID_FROM_NOTIFICATION);
      lastMsgIdFromInbox = AKDBHelper.getInstance(context).getMaxMsgIdFromMessageMaster();
      maxMsgId = Math.max(lastNotificationId, lastMsgIdFromInbox);

      //Temp
      //maxMsgId = 24332;
      Log.d(TAG, "lastNotificationId : " + lastNotificationId + ", lastMsgIdFromInbox: " + lastMsgIdFromInbox);
      UserRegData userRegData = SharedPreferencesTask.getUserRegData(context);
      UserProfile userProfile = SharedPreferencesTask.getUserProfile(context);
      if(maxMsgId > 0 && userRegData != null) {
        pullNotificationDTO.setLastMessageId(maxMsgId);
        pullNotificationDTO.setDevice(userRegData.getDevice());
        Log.d(TAG, "userProfile:" + userProfile);
        if(userProfile != null && userProfile.getSenderChannelList() != null && !userProfile.getSenderChannelList().isEmpty())
          pullNotificationDTO.setIsCoordinator(1);
        else
          pullNotificationDTO.setIsCoordinator(0);
        pullNotificationDTO.setProfileHash(SharedPreferencesTask.getProfileHash(context));
        pullNotificationDTO.setToken(SharedPreferencesTask.getToken(context));
        HttpPostAsyncTask task = new HttpPostAsyncTask(new Gson().toJson(pullNotificationDTO), "", asyncResponseListner);
        task.execute(WSConstant.pullUrl);
      }
    } catch (Exception e) {
      Log.d(TAG, "sendNotificationLog Exception : " + e.getMessage());
      e.getStackTrace();
      e.printStackTrace();
    }

  }


}
