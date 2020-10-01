package org.dadabhagwan.AKonnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.dadabhagwan.AKonnect.constants.SharedPrefConstants;
import org.dadabhagwan.AKonnect.dto.UserProfile;

public class SharedPreferencesTask {

  protected static final String TAG = "SharedPrefTask";
  String sharedPrefFileName;
  Context context;

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

}
