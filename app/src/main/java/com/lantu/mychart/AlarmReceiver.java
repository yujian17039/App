package com.lantu.mychart;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");
                LocalData localData = new LocalData(context);
               // NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_hour(), localData.get_min() , 1);
                NotificationScheduler.setReminders(context, AlarmReceiver.class);
                return;
            }
        }

        String title =  intent.getStringExtra(LocalData.ALERT_TITLE_LABEL);
        String desc= intent.getStringExtra(LocalData.ALERT_CONTENT_LABEL);


        Log.d(TAG, "onReceive: " + title + "  " + desc );
        int channel = NotificationScheduler.DAILY_REMINDER_REQUEST_CODE;

        if (title.indexOf("Injection") >0  )
        {
            channel =102;
        }

        //Trigger the notification
        NotificationScheduler.showNotification(context, MainActivity.class,title, desc, channel ) ;

        //        "Glucose Test Time", "Test Glucose Now?");

    }
}

