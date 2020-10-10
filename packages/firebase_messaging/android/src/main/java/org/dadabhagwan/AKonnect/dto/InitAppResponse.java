package org.dadabhagwan.AKonnect.dto;

import com.google.gson.annotations.SerializedName;

public class InitAppResponse {


  @SerializedName("alarm_active_flag")
  boolean alarmActiveFlag;

  @SerializedName("validate_inactive_days")
  int validateInactiveDays;
  @SerializedName("channel_version")
  int channelVersion;
  @SerializedName("config_version")
  String configVersion;
  @SerializedName("latest_ios_app_version")
  String latestIosAppVersion;
  @SerializedName("latest_android_app_version")
  String latestAndroidAppVersion;
  @SerializedName("repeat_alarm_time_in_minutes")
  int repeatAlarmTimeInMinutes;
  @SerializedName("alarm_offset_window_in_minutes")
  int alarmOffsetWindowInMinutes;

  // Custom Flag
  @SerializedName("last_seen")
  String lastSeen;

  @Override
  public String toString() {
    return "InitAppResponse{" +
      "alarmActiveFlag=" + alarmActiveFlag +
      ", validateInactiveDays=" + validateInactiveDays +
      ", channelVersion=" + channelVersion +
      ", configVersion='" + configVersion + '\'' +
      ", latestIosAppVersion='" + latestIosAppVersion + '\'' +
      ", latestAndroidAppVersion='" + latestAndroidAppVersion + '\'' +
      ", repeatAlarmTimeInMinutes=" + repeatAlarmTimeInMinutes +
      ", alarmOffsetWindowInMinutes=" + alarmOffsetWindowInMinutes +
      ", lastSeen=" + lastSeen +
      '}';
  }

  public boolean isAlarmActiveFlag() {
    //return true;
    return alarmActiveFlag;
  }

  public void setAlarmActiveFlag(boolean alarmActiveFlag) {
    this.alarmActiveFlag = alarmActiveFlag;
  }

  public int getValidateInactiveDays() {
    return validateInactiveDays;
  }

  public void setValidateInactiveDays(int validateInactiveDays) {
    this.validateInactiveDays = validateInactiveDays;
  }

  public int getChannelVersion() {
    return channelVersion;
  }

  public void setChannelVersion(int channelVersion) {
    this.channelVersion = channelVersion;
  }

  public String getConfigVersion() {
    return configVersion;
  }

  public void setConfigVersion(String configVersion) {
    this.configVersion = configVersion;
  }

  public String getLatestIosAppVersion() {
    return latestIosAppVersion;
  }

  public void setLatestIosAppVersion(String latestIosAppVersion) {
    this.latestIosAppVersion = latestIosAppVersion;
  }

  public String getLatestAndroidAppVersion() {
    return latestAndroidAppVersion;
  }

  public void setLatestAndroidAppVersion(String latestAndroidAppVersion) {
    this.latestAndroidAppVersion = latestAndroidAppVersion;
  }

  public int getRepeatAlarmTimeInMinutes() {
    return 1;
//    return repeatAlarmTimeInMinutes;
  }

  public void setRepeatAlarmTimeInMinutes(int repeatAlarmTimeInMinutes) {
    this.repeatAlarmTimeInMinutes = repeatAlarmTimeInMinutes;
  }

  public int getAlarmOffsetWindowInMinutes() {
   return 1;
//    return alarmOffsetWindowInMinutes;
  }

  public void setAlarmOffsetWindowInMinutes(int alarmOffsetWindowInMinutes) {
    this.alarmOffsetWindowInMinutes = alarmOffsetWindowInMinutes;
  }

  public String getLastSeen() {
    return lastSeen;
  }

  public void setLastSeen(String lastSeen) {
    this.lastSeen = lastSeen;
  }

}
