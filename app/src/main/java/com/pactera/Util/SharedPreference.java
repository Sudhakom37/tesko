package com.pactera.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.pactera.model.Notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by P0147845 on 23-10-2019.
 */

public class SharedPreference {

    public static final String PREFS_NAME = "TeSCO";
    public static final String FAVORITES = "notification_history";

    public SharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveHistory(Context context, List<String> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.apply();
    }

    public void addNotification(Context context, String Notification) {
        List<String> favorites = getHistory(context);
        if (favorites == null)
            favorites = new ArrayList<>();
        favorites.add(Notification);
        saveHistory(context, favorites);
    }

    public void removeNotification(Context context, Notification notification) {
        ArrayList<String> favorites = getHistory(context);
        if (favorites != null) {
            favorites.remove(notification.getName());
            saveHistory(context, favorites);
        }
    }

    /*public ArrayList<Notification> getHistory(Context context) {
        SharedPreferences settings;
        List<Notification> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            Notification[] favoriteItems = gson.fromJson(jsonFavorites,
                    Notification[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Notification>(favorites);
        } else
            return null;

        return (ArrayList<Notification>) favorites;
    }*/

    public ArrayList<String> getHistory(Context context) {
        SharedPreferences settings;
        List<String> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            String[] favoriteItems = gson.fromJson(jsonFavorites,
                    String[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else
            return null;

        return (ArrayList<String>) favorites;
    }
}
