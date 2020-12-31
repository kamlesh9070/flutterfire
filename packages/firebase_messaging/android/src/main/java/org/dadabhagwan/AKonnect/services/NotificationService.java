package org.dadabhagwan.AKonnect.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.dadabhagwan.AKonnect.AKonnectNotificationManager;
import org.dadabhagwan.AKonnect.dto.NotificationDTO;

import androidx.annotation.Nullable;

public class NotificationService extends Service {

  protected static final String TAG = "[NotService]";
  @Override
  public void onCreate() {
    super.onCreate();
  }
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    startForeground(1, AKonnectNotificationManager.getOrNotifyStickyNotification(this, true));
    //do heavy work on a background thread
    //stopSelf();
    return START_NOT_STICKY;
  }
  @Override
  public void onDestroy() {
    super.onDestroy();
  }
  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }



}

