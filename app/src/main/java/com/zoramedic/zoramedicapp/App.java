package com.zoramedic.zoramedicapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    //TODO: add responsive dimensions
    //TODO: predji projekat i obrisi nepotreban komentiran kod
    //TODO: predji sve layout xml da vidis dal sve vodi na string i vidi layout svaki sa srpskim dal odgovara
    //TODO: predji ako ima clearanceLvl 0
    //TODO: namesti layoutove razmake malo bolje

    public static final String NOTES_CHANNEL_ID = "NOTES_SERVICE_CHANNEL";
    public static final String PHARMACY_CHANNEL_ID = "PHARMACY_SERVICE_CHANNEL";
    public static final String CHAT_CHANNEL_ID = "CHAT_SERVICE_CHANNEL";


    List<NotificationChannel> notificationChannels = new ArrayList<>();

    //TODO: FIx this memory leak
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
        this.context = this;
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notesChannel = new NotificationChannel(
                    NOTES_CHANNEL_ID,
                    getString(R.string.notes),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationChannel pharmacyChannel = new NotificationChannel(
                    PHARMACY_CHANNEL_ID,
                    getString(R.string.pharmacy),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationChannel chatChannel = new NotificationChannel(
                    CHAT_CHANNEL_ID,
                    getString(R.string.chat),
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            notificationChannels.add(notesChannel);
            notificationChannels.add(pharmacyChannel);
            notificationChannels.add(chatChannel);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannels(notificationChannels);
        }
    }

    public static Context getContext() {
        return context;
    }
}


