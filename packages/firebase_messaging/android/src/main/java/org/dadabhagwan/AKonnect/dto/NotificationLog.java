package org.dadabhagwan.AKonnect.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class NotificationLog {

  @SerializedName("device_id")
  String deviceID;
  @SerializedName("device_model")
  String deviceModel;
  @SerializedName("device_os")
  String deviceOS;
  @SerializedName("os_version")
  String osVersion;
  @SerializedName("app_version")
  String appVersion;
  @SerializedName("device")
  String device;
  @SerializedName("client_id")
  String clientID;
  @SerializedName("timestamp")
  long timestamp;

  @SerializedName("date")
  String time;

  public void setFieldsFromDTO(DeviceDetail deviceDetail) {
    if(deviceDetail != null) {
      deviceID = deviceDetail.getDeviceId();
      deviceModel = deviceDetail.getDeviceModel();
      deviceOS = deviceDetail.getDeviceOs();
      osVersion = deviceDetail.getOsVersion();
      appVersion = deviceDetail.getAppVersion();
      clientID = deviceDetail.getClientId();
      timestamp = new Date().getTime();
    }
  }

  public void setFieldsFromDTO(UserRegData userRegData) {
    if(userRegData != null) {
      device = userRegData.getDevice();
    }
  }
  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public String getClientID() {
    return clientID;
  }

  public void setClientID(String clientID) {
    this.clientID = clientID;
  }

  public String getDeviceID() {
    return deviceID;
  }

  public void setDeviceID(String deviceID) {
    this.deviceID = deviceID;
  }

  public String getDeviceModel() {
    return deviceModel;
  }

  public void setDeviceModel(String deviceModel) {
    this.deviceModel = deviceModel;
  }

  public String getDeviceOS() {
    return deviceOS;
  }

  public void setDeviceOS(String deviceOS) {
    this.deviceOS = deviceOS;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

  public String getDevice() {
    return device;
  }

  public void setDevice(String device) {
    this.device = device;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return "NotificationLog{" +
      "deviceID='" + deviceID + '\'' +
      ", deviceModel='" + deviceModel + '\'' +
      ", deviceOS='" + deviceOS + '\'' +
      ", osVersion='" + osVersion + '\'' +
      ", appVersion='" + appVersion + '\'' +
      ", device='" + device + '\'' +
      ", clientID='" + clientID + '\'' +
      ", timestamp=" + timestamp +
      ", time='" + time + '\'' +
      '}';
  }
}
