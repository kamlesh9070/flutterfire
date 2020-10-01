package org.dadabhagwan.AKonnect;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import android.os.Build;

import java.util.Date;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;

public class AlarmSetupReceiver extends BroadcastReceiver {

  protected static final String TAG = "AKonnect[AlarmSetup]";

  @Override
  public void onReceive(Context context, Intent intent) {
    try {
      Log.d(TAG, "&&&&&&&&&&&&&&&&&&&&&&&&&&& Start AlarmSetupReceiver");
      setAlarm(context);
    } catch (Exception e) {
      Log.e(TAG, "Error inside AlarmSetupReceiver message." + e.getMessage(), e);
      e.printStackTrace();
    }
    Log.d(TAG, "&&&&&&&&&&&&&&&&&&&&&&&&&&& End AlarmSetupReceiver");
  }

  public static void setAlarm(Context context) {
    boolean AlarmActiveFlag = true;
    try {
      SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      AlarmActiveFlag = sharedPreferencesTask.getBoolean(SharedPrefConstants.ALARM_ACTIVE_FLAG);
      long currentTimestamp = ApplicationUtility.getCurrentTimestamp();

      if (AlarmActiveFlag) {
        int alarmInterval = sharedPreferencesTask.getInt(SharedPrefConstants.REPEAT_ALARM_TIME_IN_MINUTES);
        int offsetInterval = sharedPreferencesTask.getInt(SharedPrefConstants.ALARM_OFFSET_WINDOW_IN_SECONDS);

        alarmInterval = alarmInterval * 60 * 1000; // Converting alarmInterval to millisecs
        offsetInterval = offsetInterval * 60 * 1000; // Converting offsetInterval to millisecs
        //offsetInterval =  offsetInterval * 1000; // Converting offsetInterval to millisecs

        //Adding Random milliseconds to Alarm, to avoid concurrent hit to servers from each device
        //scheduleAlramTimestamp=	currentTimestamp + alarmInterval + (long) (Math.random()*offsetInterval);
        long tempAlarmInterval = alarmInterval + (long) (Math.random() * offsetInterval);

        Intent alarm = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringAlarm = PendingIntent.getBroadcast(context, 973132, alarm, 0);//973132 - is just id to identify the alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
          SystemClock.elapsedRealtime(), tempAlarmInterval, recurringAlarm);

        sharedPreferencesTask.saveLong(SharedPrefConstants.LAST_SEEN_TIMESTAMP, currentTimestamp);
        sharedPreferencesTask.saveString(SharedPrefConstants.NEXT_ALARM_SCHEDULED_TIME, "" + new Date(currentTimestamp + tempAlarmInterval));

        Log.d(TAG, "AlarmSetupReceiver.setAlarm ---> SystemClock.elapsedRealtime(): " + new Date(SystemClock.elapsedRealtime()) + " , \n  Next Alarm Schedule at  ----------------------->" + new Date(currentTimestamp + tempAlarmInterval)
          + "\n , Current Time is : " + new Date(currentTimestamp));

        //Toast.makeText(context, "Next Alarm Schedule at :: "+new Date(currentTimestamp + tempAlarmInterval), Toast.LENGTH_LONG).show();

      } else {
        Log.d(TAG, "AlarmSetupReceiver.setAlarm ---> Cancelled ----------------------");
        Intent alarm = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringAlarm = PendingIntent.getBroadcast(context, 973132, alarm, 0);//973132 - is just id to identify the alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(recurringAlarm);
      }
    } catch (Exception e) {
      Log.e(TAG, "Exception is AlarmSetupReceiver.setAlarm  ============> " + e.getMessage());
      e.printStackTrace();
    }

  }


}
