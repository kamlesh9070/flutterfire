package org.dadabhagwan.AKonnect.dto;

public class NotificationDTO {

    private int notId;
    private String senderAliasId;
    private String notificationTitle;
    private String notificationText;


    public int getNotId() {
        return notId;
    }

    public void setNotId(int notId) {
        this.notId = notId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getSenderAliasId() {
        return senderAliasId;
    }

    public void setSenderAliasId(String senderAliasId) {
        this.senderAliasId = senderAliasId;
    }

    @Override
    public String toString() {
        return "NotificationDTO{" +
                "notId=" + notId +
                ", senderAliasId=" + senderAliasId +
                ", notificationTitle='" + notificationTitle + '\'' +
                ", notificationText='" + notificationText + '\'' +
                '}';
    }
}
