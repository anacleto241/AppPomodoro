// br.edu.ifsuldeminas.mach.apppomodoro.AlarmReceiver.java
package br.edu.ifsuldeminas.mach.apppomodoro.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import br.edu.ifsuldeminas.mach.apppomodoro.utils.NotificationHelper;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AlarmReceiver onReceive called!");

        String message = intent.getStringExtra("message");
        if (message == null) {
            message = "Seu cronômetro de estudo terminou!";
        }

        NotificationHelper notificationHelper = new NotificationHelper();
        notificationHelper.notificar(context, "Estude por Blocos", message);
    }
}