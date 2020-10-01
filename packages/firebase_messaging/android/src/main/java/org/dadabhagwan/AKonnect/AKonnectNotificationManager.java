package org.dadabhagwan.AKonnect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import android.util.Log;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.dto.NotificationDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import me.pushy.sdk.PushReceiver;

import org.dadabhagwan.AKonnect.dbo.DBHelper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


/**
 * Created by coder2 on 14-Feb-2018.
 */

public class AKonnectNotificationManager {

  private static final String TAG = "AKonnect[NotManager]";
  private static int i = 0;
  private static final int color = 0xb71d13;
  private static final String group = "AKONNECTGROUP";
  private static final String NOTIFICATION_CHANNEL_GROUP_AKONNECT_ID = "AKONNECT";
  private static final String NOTIFICATION_CHANNEL_GROUP_AKONNECT_DESC = "AKonnect Senders";
  private static final String NOTIFICATION_CHANNEL_GROUP_SYSTEM_ID = "SYSTEM";
  private static final String NOTIFICATION_CHANNEL_GROUP_SYSTEM_DESC = "System";
  private static final String NOTIFICATION_CHANNEL_DESC_SILENT = "Silent Notification";
  private static final String NOTIFICATION_CHANNEL_ID_SILENT = "SILENT";
  private static final int AKONNECT_GROUP_ID = 2000;
  private final static int AKONNECT_NOT_ID = 2020;
  private static final long lDurationBtwTwoNotiForSound = 15 * 1000;
  //vibrate
  long[] vibrate = {500, 1000};

  static Date previousNotificationTime = null;
  static long waitMillSecond = 0;

  private Context context;
  private NotificationDTO notificationDTO;
  private static Map<String, String> titlesBySenderId = new ConcurrentHashMap<String, String>();
  private DBHelper dbHelper;

  public AKonnectNotificationManager(Context context, NotificationDTO notificationDTO) {
    this.context = context;
    this.notificationDTO = notificationDTO;

  }

  public void sendStackNotification(String pushType) {
    boolean isMsgIdExist = false;
    boolean AlarmActiveFlag = true;
    try {
      //boolean isDeviceManufacturerSupported = ApplicationUtility.isDeviceManufacturerSupported(context);
      SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_NOTIFICATION_LOG_PREF);
      SharedPreferencesTask senderAliasMasterPref = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_SENDER_ALIAS_MASTER_PREF);
      AlarmActiveFlag = sharedPreferencesTask.getBoolean(SharedPrefConstants.ALARM_ACTIVE_FLAG);
      long currentTimestamp = ApplicationUtility.getCurrentTimestamp();

      if (AlarmActiveFlag) {
        dbHelper = DBHelper.getInstance(context);
        isMsgIdExist = dbHelper.getNotificationLogByMsgId(notificationDTO.getMessageId());
      }

      if (!isMsgIdExist) {
        System.out.println(TAG + "\tNotificationDTO" + notificationDTO);
        Log.d(TAG, "NotificationDTO" + notificationDTO);
        if (notificationDTO.getChannelId() != null && titlesBySenderId.get(notificationDTO.getChannelId()) == null) {
          titlesBySenderId.put(notificationDTO.getChannelId(), notificationDTO.getChannelName());
        }
        if (notificationDTO.getChannelId() != null && senderAliasMasterPref.getString(notificationDTO.getChannelId()) != null) {
          senderAliasMasterPref.saveString(notificationDTO.getChannelId(), notificationDTO.getChannelName());
        }

        String appName = getAppName(context);
        NotificationManager notificationManager = getNotificationManager();
        // only run this code if the device is running 23 or better
        if (Build.VERSION.SDK_INT >= 23) {
          StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
          if (activeNotifications.length > 0) {
            // Wait until notification with sound is notified
            try {
              Thread.sleep(waitMillSecond);
            } catch (Exception e) {
            }
            NotificationManagerCompat.from(context).cancelAll();
            if (Build.VERSION.SDK_INT == 23)
              sendNotificationFor23API(activeNotifications);
            else
              sendNotificationAbove23API(activeNotifications);
          } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              sendNotificationAbove26API(context, notificationDTO);
            } else {
              notifyBasicNotification(context, notificationDTO);
            }
          }
        } else {
          notifyBasicNotification(context, notificationDTO);
        }
        // Insert data into NotificationMaster
        synchronized (this) {
          if (AlarmActiveFlag) {
            dbHelper.insertNotificationLog(notificationDTO.getMessageId());
            try {
              sharedPreferencesTask.saveInt(SharedPrefConstants.LAST_MSGID_FROM_NOTIFICATION, notificationDTO.getMessageId());
            } catch (Exception e) {
              Log.e(TAG, " Exception in sharedPreferencesTask " + e.getMessage());
              e.printStackTrace();
            }
          }
        }
        // Log Notification received in Firebase DB
        WebServiceCall.sendNotificationLog(context, pushType, "" + notificationDTO.getMessageId());
        //AlarmSetupReceiver.setAlarm(context);
      } else {
        Log.d(TAG, "sendStackNotification Record exist in NotificationMaster table :");
      }
    } catch (Exception e) {
      Log.e(TAG, " Exception in sendStackNotification " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println(TAG + "\tSuccesfully completed sendNotification for :" + notificationDTO);
    Log.d(TAG, "Succesfully completed sendNotification for :" + notificationDTO);
  }

  private void notifyBasicNotification(Context context, NotificationDTO notificationDTO) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
      .setContentTitle(notificationDTO.getChannelName())
      .setTicker(notificationDTO.getChannelName())
      .setContentText(notificationDTO.getNotificationTitle())
      .setSmallIcon(context.getResources().getIdentifier("secondary_icon", "drawable", context.getPackageName()))
      .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationDTO.getNotificationTitle()))
      .setContentIntent(getMainActivityPendingIntent(context))
      .setLargeIcon(ApplicationUtility.getSenderImage(notificationDTO.getChannelId(), context))
      //.setNumber(1)
      .setDefaults(Notification.DEFAULT_VIBRATE) // For single Notifications vibration will be there, for grouped Notifications vibrations is removed
      ;
    setDefaultNotificationProperty(builder, true);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      builder.setCategory(notificationDTO.getChannelId());
    }
    NotificationManager notificationManager = getNotificationManager();
    notificationManager.notify(notificationDTO.getMessageId(), builder.build());
  }

  private void setDefaultNotificationProperty(NotificationCompat.Builder builder, boolean withSound) {
    builder.setDefaults(Notification.DEFAULT_LIGHTS)
      //.setDefaults(Notification.DEFAULT_VIBRATE) // Removed to avoid continue vibration for multiple Notifications
      .setShowWhen(true)
      .setWhen(System.currentTimeMillis())
      .setAutoCancel(true);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
      builder.setColor(color);

    synchronized (AKonnectNotificationManager.class) {
      boolean toSetSound = true;
      if (previousNotificationTime != null) {
        Date currentTime = new Date();
        if ((currentTime.getTime() - previousNotificationTime.getTime()) < lDurationBtwTwoNotiForSound) {
          toSetSound = false;
          waitMillSecond = 0;
        }
      }
      if (toSetSound && withSound) {
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setDefaults(Notification.DEFAULT_ALL);
        previousNotificationTime = new Date();
        waitMillSecond = 1 * 1000;
      }
    }
  }

  private NotificationChannel getNotificationChannel(NotificationDTO notificationDTO, boolean withSound) {
    NotificationChannel notificationChannel = null;
    NotificationManager notificationManager = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      synchronized (AKonnectNotificationManager.class) {
        notificationManager = getNotificationManager();
        AudioAttributes att = new AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
          .build();

        boolean toSetSound = true;
        if (previousNotificationTime != null) {
          Date currentTime = new Date();
          if ((currentTime.getTime() - previousNotificationTime.getTime()) < lDurationBtwTwoNotiForSound) {
            toSetSound = false;
            waitMillSecond = 0;
          }
        }
        if (toSetSound && withSound) {
          previousNotificationTime = new Date();
          waitMillSecond = 1 * 1000;
          if (notificationManager.getNotificationChannel(notificationDTO.getChannelId()) != null) {
            notificationChannel = notificationManager.getNotificationChannel(notificationDTO.getChannelId());
          } else {
            notificationChannel = new NotificationChannel(notificationDTO.getChannelId(),
              titlesBySenderId.get(notificationDTO.getChannelId()), NotificationManager.IMPORTANCE_HIGH);
          }
          if (notificationChannel != null && notificationChannel.getGroup() == null) {
            NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(NOTIFICATION_CHANNEL_GROUP_AKONNECT_ID, NOTIFICATION_CHANNEL_GROUP_AKONNECT_DESC);
            notificationManager.createNotificationChannelGroup(notificationChannelGroup);
            notificationChannel.setGroup(NOTIFICATION_CHANNEL_GROUP_AKONNECT_ID);
          }
          notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), att);
          //Moved below code to Single Notification builder to avoid continue vibration for multiple Notifications
                    /*notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(vibrate);*/
        } else {
          if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID_SILENT) != null) {
            notificationChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID_SILENT);
            if (notificationChannel.getImportance() > NotificationManager.IMPORTANCE_LOW) {
              notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID_SILENT);
              notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_SILENT,
                NOTIFICATION_CHANNEL_DESC_SILENT, NotificationManager.IMPORTANCE_LOW);
            }
          } else {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_SILENT,
              NOTIFICATION_CHANNEL_DESC_SILENT, NotificationManager.IMPORTANCE_LOW);
          }

          if (notificationChannel != null && notificationChannel.getGroup() == null) {
            NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup(NOTIFICATION_CHANNEL_GROUP_SYSTEM_ID, NOTIFICATION_CHANNEL_GROUP_SYSTEM_DESC);
            notificationManager.createNotificationChannelGroup(notificationChannelGroup);
            notificationChannel.setGroup(NOTIFICATION_CHANNEL_GROUP_SYSTEM_ID);
          }
          notificationChannel.setSound(null, null);
          Log.d(TAG, "getNotificationChannel  Inside Else setting sound as NULL --> ");
        }

        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        //notificationChannel.setShowBadge(true);
        Log.d(TAG, "getNotificationChannel  toSetSound --> " + toSetSound + ", withSound --> " + withSound);
      }
    }
    return notificationChannel;
  }

  private static String getAppName(Context context) {
    // Attempt to determine app name via package manager
    return context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
  }

  private PendingIntent getMainActivityPendingIntent(Context context) {
    System.out.println("################# Inside AKonnectNotificationManager getMainActivityPendingIntent --> Ends");
    // Get launcher activity intent
    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getApplicationContext().getPackageName());

    // Make sure to update the activity if it exists
    launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

    System.out.println("################# Inside AKonnectNotificationManager getMainActivityPendingIntent --> Ends");
    // Convert intent into pending intent
    return PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

  }

  private NotificationManager getNotificationManager() {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    return notificationManager;
  }

  void sendNotificationAbove23API(StatusBarNotification[] activeNotifications) {
    if (Build.VERSION.SDK_INT >= 23) {
      StringBuilder grpMsg = new StringBuilder();
      int totalMessages = 0;
      NotificationManager notificationManager = getNotificationManager();
      Map<String, List<StatusBarNotification>> groupedNotification = new HashMap<String, List<StatusBarNotification>>();

      Log.d(TAG, "sendNotificationAbove23API  activeNotifications --> " + activeNotifications.toString());

      // step through all the active StatusBarNotifications and load groupedNotification
      for (StatusBarNotification sbn : activeNotifications) {
        Log.d(TAG, "sendNotificationAbove23API  StatusBarNotification -->  " + sbn.toString());
        Log.d(TAG, "sendNotificationAbove23API  sbn.getNotification() -->  " + sbn.getNotification().toString());
        String category = (String) sbn.getNotification().category;
        if (!group.equals(category)) {
          List<StatusBarNotification> notifications = groupedNotification.get(category);
          if (notifications == null) {
            notifications = new ArrayList<StatusBarNotification>();
            groupedNotification.put(category, notifications);
          }
          notifications.add(sbn);
        }
      }

      int k = 40;
      boolean isNotificationAdded = false;

      Set<Map.Entry<String, List<StatusBarNotification>>> entrySet = groupedNotification.entrySet();
      int totalGroup = entrySet.size();
      for (Map.Entry<String, List<StatusBarNotification>> entry : entrySet) {
        String category = entry.getKey();
        List<StatusBarNotification> notifications = entry.getValue();
        List<String> lines = new ArrayList();
        for (StatusBarNotification activeSbn : notifications) {
          String template = (String) activeSbn.getNotification().extras.get(NotificationCompat.EXTRA_TEMPLATE);
          if (template != null && template.equalsIgnoreCase("android.app.Notification$InboxStyle")) {
            CharSequence[] charSequences = (CharSequence[]) activeSbn.getNotification().extras.get(NotificationCompat.EXTRA_TEXT_LINES);
            String inboxtTitle = (String) activeSbn.getNotification().extras.get(NotificationCompat.EXTRA_TITLE);
            for (CharSequence line : charSequences) {
              lines.add(line.toString());
            }
          } else {
            String stackNotificationLine = (String) activeSbn.getNotification().extras.get(NotificationCompat.EXTRA_TEXT);
            if (stackNotificationLine != null) {
              lines.add(stackNotificationLine);
            }
          }
        }
        if (category.equalsIgnoreCase(notificationDTO.getChannelId())) {
          lines.add(notificationDTO.getNotificationTitle());
          isNotificationAdded = true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          notifyInboxNotificatonAbove26(category, lines, k++, grpMsg);
        } else {
          notifyInboxNotificaton(category, lines, k++, grpMsg);
        }
        totalMessages = totalMessages + lines.size();
      }
      if (!isNotificationAdded) {
        String channelId = notificationDTO.getChannelId();
        List<String> lines = new ArrayList(1);
        lines.add(notificationDTO.getNotificationTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          notifyInboxNotificatonAbove26(channelId, lines, k++, grpMsg);
        } else {
          notifyInboxNotificaton(channelId, lines, k++, grpMsg);
        }
        totalMessages++;
        totalGroup++;
      }

      String totalMsg = new StringBuilder(String.valueOf(totalMessages))
        .append(" messages from ")
        .append(totalGroup)
        .append(" groups")
        .toString();
      //Notification.Builder groupBuilder;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder groupBuilder;
        NotificationChannel notificationChannel = getNotificationChannel(notificationDTO, true);
        groupBuilder = new Notification.Builder(context, notificationChannel.getId());

        Notification.InboxStyle inbox = new Notification.InboxStyle();
        inbox.setSummaryText(totalMsg);
        inbox.addLine(grpMsg);

        groupBuilder.setContentTitle("AKonnect")
          .setContentText(grpMsg)
          .setGroupSummary(true)
          .setGroup(group)
          .setCategory(group)
          .setSmallIcon(context.getResources().getIdentifier("secondary_icon", "drawable", context.getPackageName()))
          .setLargeIcon(ApplicationUtility.getSenderImage(notificationDTO.getChannelId(), context))
          .setStyle(inbox)
          .setContentIntent(getMainActivityPendingIntent(context))
          .setShowWhen(true)
          .setWhen(System.currentTimeMillis())
          .setColor(color)
          //.setNumber(totalMessages)
          .setAutoCancel(true)
        ;
        //setDefaultNotificationPropertyAbove26(groupBuilder,true,notificationChannel);
        notificationManager.createNotificationChannel(notificationChannel);

        notificationManager.notify(AKONNECT_GROUP_ID, groupBuilder.build());

        Log.d(TAG, "sendNotificationAbove23API  mChannel --> " + notificationChannel.toString() + "\n groupBuilder --> " + groupBuilder.toString());
      } else {
        NotificationCompat.Builder groupBuilder;
        groupBuilder = new NotificationCompat.Builder(context);
        NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
        inbox.setSummaryText(totalMsg);
        inbox.addLine(grpMsg);

        groupBuilder.setContentTitle("AKonnect")
          .setContentText(grpMsg)
          .setGroupSummary(true)
          .setGroup(group)
          .setCategory(group)
          .setSmallIcon(context.getResources().getIdentifier("secondary_icon", "drawable", context.getPackageName()))
          .setLargeIcon(ApplicationUtility.getSenderImage(notificationDTO.getChannelId(), context))
          .setStyle(inbox)
          .setContentIntent(getMainActivityPendingIntent(context))
        //.setNumber(totalMessages)
        ;

        setDefaultNotificationProperty(groupBuilder, true);
        notificationManager.notify(AKONNECT_GROUP_ID, groupBuilder.build());
      }

    }
  }

  private void notifyInboxNotificaton(String category, List<String> lines, int notId, StringBuilder grpMsg) {
    Log.d(TAG, "notifyInboxNotificaton  category --> " + category + "\n lines --> " + lines.toString() + "\n notId --> " + notId + "\n grpMsg --> " + grpMsg.toString());
    NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
    int lineCount = 0;
    String notificationLine = null;
    for (String line : lines) {
      inbox.addLine(line);
      notificationLine = line;
      lineCount++;
    }
    inbox.setSummaryText(lineCount + " Messages");
    StringBuilder strBuilder = new StringBuilder(getGroupName(category));
    if (lineCount > 1) {
      strBuilder.append(" (")
        .append(lineCount)
        .append(" messages")
        .append(")")
        .toString();
    }

    String notificationTitle = strBuilder.toString();
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
      .setContentTitle(notificationTitle)
      .setTicker(notificationTitle)
      .setCategory(category)
      .setContentText(notificationLine)
      .setSmallIcon(context.getResources().getIdentifier("secondary_icon", "drawable", context.getPackageName()))
      .setLargeIcon(ApplicationUtility.getSenderImage(category, context))
      .setStyle(inbox)
      .setGroup(group)
      //.setNumber(lineCount)
      .setContentIntent(getMainActivityPendingIntent(context));
    setDefaultNotificationProperty(builder, false);
    Notification stackNotification = builder.build();
    NotificationManager notificationManager = getNotificationManager();
    notificationManager.notify(getAppName(context), notId, stackNotification);

    grpMsg.append(getGroupName(category))
      .append(" : ")
      /*.append(lineCount+ " Messages")
      .append(" :: ")
      .append(lines.toString())*/
      .append(notificationLine)
      .append("\n");
  }

  private void notifyInboxNotificatonAbove26(String category, List<String> lines, int notId, StringBuilder grpMsg) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Notification.Builder groupBuilder;
      NotificationManager notificationManager = getNotificationManager();
      NotificationChannel notificationChannel = getNotificationChannel(notificationDTO, false);
      groupBuilder = new Notification.Builder(context, notificationChannel.getId());

      Notification.InboxStyle inbox = new Notification.InboxStyle();
      int lineCount = 0;
      String notificationLine = null;
      for (String line : lines) {
        inbox.addLine(line);
        notificationLine = line;
        lineCount++;
      }
      inbox.setSummaryText(lineCount + " Messages");
      StringBuilder strBuilder = new StringBuilder(getGroupName(category));
      if (lineCount > 1) {
        strBuilder.append(" (")
          .append(lineCount)
          .append(" messages")
          .append(")")
          .toString();
      }

      String notificationTitle = strBuilder.toString();
      groupBuilder
        .setContentTitle(notificationTitle)
        .setTicker(notificationTitle)
        .setCategory(category)
        .setContentText(notificationLine)
        .setSmallIcon(context.getResources().getIdentifier("secondary_icon", "drawable", context.getPackageName()))
        .setLargeIcon(ApplicationUtility.getSenderImage(category, context))
        .setStyle(inbox)
        .setGroup(group)
        .setContentIntent(getMainActivityPendingIntent(context))
        .setWhen(System.currentTimeMillis())
        .setColor(color)
        .setAutoCancel(true)
        //.setNumber(lineCount)
        .setShowWhen(true);
      //setDefaultNotificationPropertyAbove26(groupBuilder, false,notificationChannel);
      notificationManager.createNotificationChannel(notificationChannel);
      Notification stackNotification = groupBuilder.build();
      notificationManager.notify(getAppName(context), notId, stackNotification);

      grpMsg.append(getGroupName(category))
        .append(" : ")
        /*.append(lineCount+ " Messages")
        .append(" :: ")
        .append(lines.toString())*/
        .append(notificationLine)
        .append("\n");
    }
  }

  private String getGroupName(String senderAliasId) {
    String groupName = senderAliasId;
    try {
      SharedPreferencesTask senderAliasMasterPref = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_SENDER_ALIAS_MASTER_PREF);
      if (senderAliasId != null && titlesBySenderId.get(senderAliasId) != null) {
        groupName = titlesBySenderId.get(senderAliasId);
        Log.d(TAG, "getGroupName INSIDE HASHMAP senderAliasId --> " + senderAliasId + "\n groupName --> " + groupName);
      }
      if (senderAliasId != null && senderAliasId.equals(groupName) && senderAliasMasterPref.getString(senderAliasId) != null) {
        groupName = senderAliasMasterPref.getString(senderAliasId);
        Log.d(TAG, "getGroupName INSIDE SHAREDPREF senderAliasId --> " + senderAliasId + "\n groupName --> " + groupName);
      }
    } catch (Exception e) {
      Log.e(TAG, "getGroupName  Exception --> " + e.getMessage());
      e.printStackTrace();
    }

    //Log.v(TAG,"getGroupName  senderAliasId --> "+senderAliasId +"\n groupName --> "+groupName);
    return groupName;
  }

  void sendNotificationFor23API(StatusBarNotification[] activeNotifications) {
    if (Build.VERSION.SDK_INT == 23) {
      NotificationManager notificationManager = getNotificationManager();
      NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();
      int lineCount = 0;
      Set<String> uniqueGroupName = new HashSet<String>();
      // step through all the active StatusBarNotifications and
      for (StatusBarNotification sbn : activeNotifications) {
        String template = (String) sbn.getNotification().extras.get(NotificationCompat.EXTRA_TEMPLATE);
        if (template.equalsIgnoreCase("android.app.Notification$InboxStyle")) {
          CharSequence[] charSequences = (CharSequence[]) sbn.getNotification().extras.get(NotificationCompat.EXTRA_TEXT_LINES);
          for (CharSequence line : charSequences) {
            inbox.addLine(line);
            String[] contents = String.valueOf(line).split(":");
            if (contents.length > 0) {
              uniqueGroupName.add(contents[0]);
            }
            lineCount++;
          }
        } else {
          String title = (String) sbn.getNotification().extras.get(NotificationCompat.EXTRA_TITLE);
          uniqueGroupName.add(title);
          String stackNotificationLine = (String) sbn.getNotification().extras.get(NotificationCompat.EXTRA_TEXT);
          if (stackNotificationLine != null) {
            inbox.addLine(title + ": " + stackNotificationLine);
            lineCount++;
          }
        }
      }

      inbox.addLine(notificationDTO.getChannelName() + ": " + notificationDTO.getNotificationTitle());
      uniqueGroupName.add(notificationDTO.getChannelName());
      lineCount++;

      inbox.setSummaryText(lineCount + " New Messages from " + uniqueGroupName.size() + " groups");
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
        .setContentTitle(lineCount + " New Messages from " + uniqueGroupName.size() + " groups")
        .setTicker(lineCount + " Messages from " + uniqueGroupName.size() + " groups")
        .setContentText(lineCount + " Messages from " + uniqueGroupName.size() + " groups")
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(context.getResources().getIdentifier("secondary_icon", "drawable", context.getPackageName()))
        .setLargeIcon(ApplicationUtility.getSenderImage(null, context))
        .setStyle(inbox)
        //.setNumber(lineCount)
        .setContentIntent(getMainActivityPendingIntent(context));
      setDefaultNotificationProperty(builder, true);
      Notification stackNotification = builder.build();
      notificationManager.notify(getAppName(context), AKONNECT_NOT_ID, stackNotification);
    }
  }

  void sendNotificationAbove26API(Context context, NotificationDTO notificationDTO) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManager notificationManager = getNotificationManager();
      NotificationChannel notificationChannel = getNotificationChannel(notificationDTO, true);
      Notification.Builder builder;

      notificationChannel.enableVibration(true);
      notificationChannel.setVibrationPattern(vibrate); // setVibration
      builder = new Notification.Builder(context, notificationChannel.getId());

      //NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
      builder.setContentTitle(notificationDTO.getChannelName())
        .setTicker(notificationDTO.getChannelName())
        .setContentText(notificationDTO.getNotificationTitle())
        .setSmallIcon(context.getResources().getIdentifier("secondary_icon", "drawable", context.getPackageName()))
        .setStyle(new Notification.BigTextStyle().bigText(notificationDTO.getNotificationTitle()))
        .setContentIntent(getMainActivityPendingIntent(context))
        .setLargeIcon(ApplicationUtility.getSenderImage(notificationDTO.getChannelId(), context))
        .setWhen(System.currentTimeMillis())
        .setColor(color)
        .setAutoCancel(true)
        .setCategory(notificationDTO.getChannelId())
        .setShowWhen(true)
      //.setNumber(1)
      ;

      //setDefaultNotificationPropertyAbove26(builder,true,notificationChannel);
      notificationManager.createNotificationChannel(notificationChannel);
      notificationManager.notify(notificationDTO.getMessageId(), builder.build());
    }
  }


}
