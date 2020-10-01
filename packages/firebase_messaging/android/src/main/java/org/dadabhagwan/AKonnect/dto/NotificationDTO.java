package org.dadabhagwan.AKonnect.dto;

import com.google.gson.annotations.SerializedName;

public class NotificationDTO {

  String notificationTitle;

  @SerializedName("guj_title")
  String gujTitle;
  @SerializedName("hindi_title")
  String hindiTitle;
  @SerializedName("eng_title")
  String engTitle;
  @SerializedName("message_id")
  int messageId;
  @SerializedName("channel_name")
  String channelName;
  @SerializedName("sender_subscriber")
  String senderSubscriber;
  @SerializedName("channel_id")
  String channelId;

  @SerializedName("avatar_url")
  String avatarUrl;


  @Override
  public String toString() {
    return "NotificationDTO{" +
      "notificationTitle='" + notificationTitle + '\'' +
      ", gujTitle='" + gujTitle + '\'' +
      ", hindiTitle='" + hindiTitle + '\'' +
      ", engTitle='" + engTitle + '\'' +
      ", messageId=" + messageId +
      ", channelName='" + channelName + '\'' +
      ", senderSubscriber='" + senderSubscriber + '\'' +
      ", channelId='" + channelId + '\'' +
      ", avatarUrl='" + avatarUrl + '\'' +
      '}';
  }

  public int getMessageId() {
    return messageId;
  }

  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  public String getNotificationTitle() {
    return notificationTitle;
  }

  public void setNotificationTitle(String notificationTitle) {
    this.notificationTitle = notificationTitle;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getGujTitle() {
    return gujTitle;
  }

  public void setGujTitle(String gujTitle) {
    this.gujTitle = gujTitle;
  }

  public String getHindiTitle() {
    return hindiTitle;
  }

  public void setHindiTitle(String hindiTitle) {
    this.hindiTitle = hindiTitle;
  }

  public String getEngTitle() {
    return engTitle;
  }

  public void setEngTitle(String engTitle) {
    this.engTitle = engTitle;
  }

  public String getSenderSubscriber() {
    return senderSubscriber;
  }

  public void setSenderSubscriber(String senderSubscriber) {
    this.senderSubscriber = senderSubscriber;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
}
