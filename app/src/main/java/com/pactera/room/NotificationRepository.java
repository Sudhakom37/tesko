package com.pactera.room;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

class NotificationRepository {

    private NotificationDAO mWordDao;
    private LiveData<List<Notification>> mAllWords;

    // Note that in order to unit test the NotificationRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples

    NotificationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mWordDao = db.contactDAO();
        mAllWords = mWordDao.getContacts();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Notification>> getAllWords() {
        return mAllWords;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Notification word) {
        AppDatabase.databaseWriteExecutor.execute(() -> mWordDao.insert(word));
    }
}