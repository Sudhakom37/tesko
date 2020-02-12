package com.pactera.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "notification")
public class Notification {


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "text")
    private String notificationText;
    @NonNull
    @ColumnInfo(name = "date")
    private Long date;
    @NonNull
    public Long getDate() {
        return date;
    }

    public void setDate( @NonNull Long date) {
        this.date = date;
    }

    @NonNull
    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(@NonNull String notificationText) {
        this.notificationText = notificationText;
    }
}
