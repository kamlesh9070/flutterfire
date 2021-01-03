package org.dadabhagwan.AKonnect.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.dadabhagwan.AKonnect.AlarmSetupReceiver;
import org.dadabhagwan.AKonnect.ApplicationUtility;
import org.dadabhagwan.AKonnect.InternetServiceConnectivityReceiver;
import org.dadabhagwan.AKonnect.SharedPreferencesTask;
import org.dadabhagwan.AKonnect.WebServiceCall;
import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.dto.InitAppResponse;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkSchedulerService extends JobService implements
  ConnectivityReceiver.ConnectivityReceiverListener {
  Context context = null;
  private static final String TAG = NetworkSchedulerService.class.getSimpleName();

  private ConnectivityReceiver mConnectivityReceiver;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, "Service created");
    mConnectivityReceiver = new ConnectivityReceiver(this);
  }



  /**
   * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
   * activity and this service can communicate back and forth. See "setUiCallback()"
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "onStartCommand");
    return START_NOT_STICKY;
  }

  @Override
  public boolean onStartJob(JobParameters params) {
    Log.i(TAG, "onStartJob" + mConnectivityReceiver);
    registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    return true;
  }

  @Override
  public boolean onStopJob(JobParameters params) {
    Log.i(TAG, "onStopJob");
    unregisterReceiver(mConnectivityReceiver);
    return true;
  }

  @Override
  public void onNetworkConnectionChanged(boolean isConnected) {
    Log.d(TAG, "onNetworkConnectionChanged");
    pullMsgFromServer(this);
    /*String message = isConnected ? "Good! Connected to Internet" : "Sorry! Not connected to internet";
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();*/

  }


  public void pullMsgFromServer(Context context) {
    boolean AlarmActiveFlag = true;
    try {
      InitAppResponse initAppResponse = SharedPreferencesTask.getInitAppResponse(context);
      Log.d(TAG, "initAppResponse:" + initAppResponse);
      if(initAppResponse != null) {
        AlarmActiveFlag = SharedPreferencesTask.getInitAppResponse(context).isAlarmActiveFlag();
        if (ApplicationUtility.isOnline(context) == true && AlarmActiveFlag) {
          fetchMsgFromServer(context);
        }
      }
    } catch (Exception e) {
      Log.e(TAG, "Error inside InternetServiceConnectivityReceiver message." + e.getMessage(), e);
      e.printStackTrace();
    }
  }

  public void fetchMsgFromServer(Context context) {
    Log.d(TAG, "Inside InternetServiceConnectivityReceiver.fetchMsgFromServer ");
    try {
      SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      long lastSeenTimestamp = sharedPreferencesTask.getLong(SharedPrefConstants.LAST_SEEN_TIMESTAMP);
      InitAppResponse initAppResponse = SharedPreferencesTask.getInitAppResponse(context);
      int repeatAlramTimeInMin = initAppResponse.getRepeatAlarmTimeInMinutes();
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
  public void onPostExecute(String output) {
    try {
      ApplicationUtility.handlePullNotificationRes(context, output);
      AlarmSetupReceiver.setAlarm(context);
    } catch (Exception e) {
      Log.e(TAG, "processFinish output Exception:: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
