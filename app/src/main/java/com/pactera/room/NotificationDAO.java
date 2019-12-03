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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insert(Notification... contacts);
 
    @Update
    public void update(Notification... contacts);
 
    @Delete
    public void delete(Notification contact);

    @Query("SELECT * FROM notification ORDER BY date DESC limit 5 ")
    public LiveData<List<Notification>> getContacts();

}