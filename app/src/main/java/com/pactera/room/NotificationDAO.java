package com.pactera.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface NotificationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Notification... contacts);
 
    @Update
    void update(Notification... contacts);
 
    @Delete
    void delete(Notification contact);

    @Query("SELECT * FROM notification ORDER BY date DESC limit 5 ")
    LiveData<List<Notification>> getContacts();

}