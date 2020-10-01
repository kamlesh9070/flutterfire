package org.dadabhagwan.AKonnect.dto;

import com.google.gson.annotations.SerializedName;

public class NotificationData {


  @SerializedName("guj_title")
  String gujTitle;
  @SerializedName("hindi_title")
  String hindiTitle;
  @SerializedName("eng_title")
  String engTitle;
  @SerializedName("message_id")
  String messageId;
  @SerializedName("channel_name")
  String channelName;
  @SerializedName("sender_subscriber")
  String senderSubscriber;
  @SerializedName("channel_id")
  String channelId;

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

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  public String getSenderSubscriber() {
    return senderSubscriber;
  }

  public void setSenderSubscriber(String senderSubscriber) {
    this.senderSubscriber = senderSubscriber;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  @Override
  public String toString() {
    return "NotificationData{" +
      "gujTitle='" + gujTitle + '\'' +
      ", hindiTitle='" + hindiTitle + '\'' +
      ", engTitle='" + engTitle + '\'' +
      ", messageId='" + messageId + '\'' +
      ", channelName='" + channelName + '\'' +
      ", senderSubscriber='" + senderSubscriber + '\'' +
      ", channelId='" + channelId + '\'' +
      '}';
  }
}
