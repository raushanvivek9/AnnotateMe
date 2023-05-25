package com.example.anno_tool.Notification;

public class NotificationPush {
    private NotificationDatas notification;
    private String to;

    public NotificationPush(NotificationDatas notification, String to) {
        this.notification = notification;
        this.to = to;
    }

    public NotificationDatas getNotification() {
        return notification;
    }

    public void setNotification(NotificationDatas notification) {
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
