package org.dadabhagwan.AKonnect;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.dto.NotificationDTO;
import org.json.JSONArray;
import org.json.JSONObject;

public class InternetServiceConnectivityReceiver extends BroadcastReceiver implements AsyncResponseListner {

  public static final String PREFS_NAME = "PushyRestartPrefs";
  private static final String Last_Pushy_Restart = "LastPushyRestart";
  private static final long Min_Restart_Interval = 5 * 60 * 1000; // 5 Mins
  private static final String TAG = "AKonnect[Conn]";
  Context context = null;

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "Inside InternetServiceConnectivityReceiver.onReceive ");
    boolean AlarmActiveFlag = true;
    try {
      SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      AlarmActiveFlag = sharedPreferencesTask.getBoolean(SharedPrefConstants.ALARM_ACTIVE_FLAG);

      if (ApplicationUtility.isOnline(context) == true && AlarmActiveFlag) {
        fetchMsgFromServer(context);
      }
    } catch (Exception e) {
      Log.e(TAG, "Error inside InternetServiceConnectivityReceiver message." + e.getMessage(), e);
      e.printStackTrace();
    }
  }


  private void fetchMsgFromServer(Context context) {
    Log.d(TAG, "Inside InternetServiceConnectivityReceiver.fetchMsgFromServer ");
    try {
      SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      long lastSeenTimestamp = sharedPreferencesTask.getLong(SharedPrefConstants.LAST_SEEN_TIMESTAMP);
      int repeatAlramTimeInMin = sharedPreferencesTask.getInt(SharedPrefConstants.REPEAT_ALARM_TIME_IN_MINUTES);
      long currentTimestamp = ApplicationUtility.getCurrentTimestamp();

      // We only call API if time difference is greater than Alarm Interval time
      if ((currentTimestamp - lastSeenTimestamp) > (repeatAlramTimeInMin * 60 * 1000)) {
        WebServiceCall.fetchMsgFromServer(context, this);
      } else {
        Log.d(TAG, "Inside Else condition fetchMsgFromServer, where Device Last seen on " + new java.util.Date(lastSeenTimestamp));
      }
      this.context = context;
    } catch (Exception e) {
      Log.e(TAG, "Error inside InternetServiceConnectivityReceiver message." + e.getMessage(), e);
      e.printStackTrace();
    }

  }

  //Here you will receive the result fired from async class of onPostExecute(result) method.
  @Override
  public void onPostExecute(JSONObject output) {
    try {
      Log.d(TAG, "processFinish output :: " + output);
      Log.d(TAG, "processFinishoutput.toString() :: " + output.toString());
      Log.d(TAG, "processFinish output.getJSONArray.length----------------->" + output.getJSONArray("result").length());

      JSONArray resultArray = output.getJSONArray("result");
      boolean processFlag = output.getBoolean("processFlag");
      String error = output.getString("error");

      if (processFlag && resultArray.length() > 0) {
        ApplicationUtility.generateNotificationsForFetchMessages(context, resultArray);
      }
      AlarmSetupReceiver.setAlarm(context);
    } catch (Exception e) {
      Log.e(TAG, "processFinish output Exception:: " + e.getMessage());
      e.printStackTrace();
    }

  }

}
