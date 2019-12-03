package com.pactera.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class NotificationViewModel extends AndroidViewModel {

   private NotificationRepository mRepository;

   private LiveData<List<Notification>> mAllWords;

   public NotificationViewModel(Application application) {
       super(application);
       mRepository = new NotificationRepository(application);
       mAllWords = mRepository.getAllWords();
   }

   public LiveData<List<Notification>> getAllWords() { return mAllWords; }

   public void insert(Notification word) { mRepository.insert(word); }
}