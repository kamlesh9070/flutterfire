package org.dadabhagwan.AKonnect.dto;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class NotificationPullRes {

  @SerializedName("message_list")
  List<NotificationDTO> notificationDTOList;

  @SerializedName("processFlag")
  boolean processFlag = true;
  public List<NotificationDTO> getNotificationDTOList() {
    return notificationDTOList;
  }

  public void setNotificationDTOList(List<NotificationDTO> notificationDTOList) {
    this.notificationDTOList = notificationDTOList;
  }

  public boolean isProcessFlag() {
    return processFlag;
  }

  public void setProcessFlag(boolean processFlag) {
    this.processFlag = processFlag;
  }

  @Override
  public String toString() {
    return "NotificationPullRes{" +
      "notificationDTOList=" + notificationDTOList +
      ", processFlag=" + processFlag +
      '}';
  }
}
