package org.dadabhagwan.AKonnect.dto;

public class NotificationDTO {

  private int messageId;
  private String channelId;
  private String channelName;
  private String notificationTitle;


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

  @Override
  public String toString() {
    return "NotificationDTO{" +
      "notId=" + messageId +
      ", senderAliasId=" + channelId +
      ", notificationTitle='" + channelName + '\'' +
      ", notificationText='" + notificationTitle + '\'' +
      '}';
  }
}
