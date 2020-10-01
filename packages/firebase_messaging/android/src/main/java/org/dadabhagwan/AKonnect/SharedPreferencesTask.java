package org.dadabhagwan.AKonnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.dto.ChannelDetails;
import org.dadabhagwan.AKonnect.dto.UserProfile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesTask {

  protected static final String TAG = "SharedPrefTask";
  String sharedPrefFileName;
  Context context;

  private static Map<String, SharedPreferencesTask> sharedPreferencesTaskMap = new HashMap<>();

  private static ArrayList<ChannelDetails> channelDetails;
  private static Map<String, ChannelDetails> channelById = new HashMap<>();
  private static UserProfile userProfile;

  public static SharedPreferencesTask getSharedPreferenceTask(Context context, String sharedPrefFileName) {
    SharedPreferencesTask sTask = sharedPreferencesTaskMap.get(sharedPrefFileName);
    if (sTask == null) {
      sTask = new SharedPreferencesTask(context, sharedPrefFileName);
      sharedPreferencesTaskMap.put(sharedPrefFileName, sTask);
    }
    return sTask;
  }

  public SharedPreferencesTask(Context context, String sharedPrefFileName) {
    this.sharedPrefFileName = sharedPrefFileName;
    this.context = context;
  }

  public void saveString(String key, String value) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(key, value);
    editor.commit();
  }

  public String getString(String key) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    String str = "";
    if (sharedPreferences.contains(key)) {
      str = sharedPreferences.getString(key, "");
    }
    return str;
  }

  public SharedPreferences getSharedPref() {
    return context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
  }

  public void saveInt(String key, int value) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(key, value);
    editor.commit();
  }

  public int getInt(String key) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);

    if (sharedPreferences.contains(key)) {
      return sharedPreferences.getInt(key, 0);
    } else {
      return 0;
    }
  }

  public void saveLong(String key, long value) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putLong(key, value);
    editor.commit();
  }

  public long getLong(String key) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);

    if (sharedPreferences.contains(key)) {
      return sharedPreferences.getLong(key, 0);
    } else {
      return 0;
    }
  }

  public void saveBoolean(String key, boolean value) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(key, value);
    editor.commit();
  }

  public boolean getBoolean(String key) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    if (sharedPreferences.contains(key)) {
      return sharedPreferences.getBoolean(key, false);
    } else {
      return true;
    }
  }

  public void clearPreferences(Context context) {
    context.getSharedPreferences(sharedPrefFileName, 0).edit().clear().commit();
  }


  public void removePreference(String key) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefFileName,
      Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.remove(key);
    editor.commit();
  }


  public static UserProfile getUserProfile(Context context) {
    try {
      SharedPreferencesTask sharedPreferencesTask = new SharedPreferencesTask(context, SharedPrefConstants.FILE_NAME_APP_MAIN_PREF);
      String userProfileJson = sharedPreferencesTask.getString(SharedPrefConstants.FLUTTER_USERPROFILE);
      Log.d(TAG, "$$$$$ userProfileJson:" + userProfileJson);
      return new Gson().fromJson(userProfileJson, UserProfile.class);
    } catch (Exception e) {
      Log.e(TAG, "Error while getting User Profile", e);
    }
    return null;
  }

  public static ChannelDetails getChannelDetails(String channelId, Context context) {
    getChannelDetailsList(context);
    if (channelById.isEmpty()) {
      for (ChannelDetails channelDetails : channelDetails) {
        channelById.put(channelDetails.getChannelId(), channelDetails);
      }
    }
    return channelById.get(channelId);
  }

  public static ArrayList<ChannelDetails> getChannelDetailsList(Context context) {
    try {
      if (channelDetails == null) {
        SharedPreferencesTask sharedPreferencesTask = SharedPreferencesTask.getSharedPreferenceTask(context, SharedPrefConstants.FILE_NAME_APP_MAIN_PREF);
        String channelsStr = sharedPreferencesTask.getString(SharedPrefConstants.FLUTTER_CHANNELDETAILS);
        if (!ApplicationUtility.isStrNullOrEmpty(channelsStr)) {
          Type channelListType = new TypeToken<ArrayList<ChannelDetails>>() {
          }.getType();
          channelDetails = new Gson().fromJson(channelsStr, channelListType);
        }
      }
      return channelDetails;
    } catch (Exception e) {
      Log.e(TAG, "Error while getting User Profile", e);
    }
    return null;
  }

}
