package com.lantu.mychart;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


public class NotificationScheduler
{
    public static final int DAILY_REMINDER_REQUEST_CODE=101;
    public static final String TAG="NotificationScheduler";
    public static final String CHANNEL_ID="lantu";





    public static void setReminders(Context context, Class<?> cls)
    {


        Log.d(TAG, "SetReminders "  );
        // cancel already scheduled reminders
        cancelReminder(context,cls);

        LocalData ld = new LocalData(context);
        setReminderN(context, cls, ld.get_hour(), ld.get_min(), 1 );

        setReminderN(context, cls, ld.get_ihour(), ld.get_imin(), 2 );

    }

    public static void setReminderN(Context context,Class<?> cls,int hour, int min, int type)
    {
        Calendar calendar = Calendar.getInstance();

        Calendar setcalendar = Calendar.getInstance();
        setcalendar.setTimeInMillis(System.currentTimeMillis());
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);

        Log.d(TAG, "SetReminderN " + type);
        // cancel already scheduled reminders

        if(setcalendar.before(calendar))
            setcalendar.add(Calendar.DATE,1);

        // Enable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        Intent intent1 = new Intent(context, cls);
        int request_cd = DAILY_REMINDER_REQUEST_CODE ;
        if( type == 1 )   // glucose
        {
            intent1.putExtra(LocalData.ALERT_TITLE_LABEL, "Glucose Test");
            intent1.putExtra(LocalData.ALERT_CONTENT_LABEL, "Glucose Testing Time is coming");

        } else
        {
            intent1.putExtra(LocalData.ALERT_TITLE_LABEL, "Insulin Injection");
            intent1.putExtra(LocalData.ALERT_CONTENT_LABEL, "Insulin Injection Time is coming");
            request_cd = 102;
        }
        Log.d(TAG, "SetAlertN " + type + "   " + request_cd );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, request_cd, intent1, PendingIntent.FLAG_UPDATE_CURRENT );
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //  am.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
        am.setRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_HOUR , pendingIntent);

    }


    public static void setReminder(Context context,Class<?> cls,int hour, int min, int type)
    {
        Calendar calendar = Calendar.getInstance();

        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);

        Log.d(TAG, "SetReminder " + type);
        // cancel already scheduled reminders
        cancelReminder(context,cls);

        if(setcalendar.before(calendar))
            setcalendar.add(Calendar.DATE,1);

        // Enable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


       Intent intent1 = new Intent(context, cls);
       int request_cd = DAILY_REMINDER_REQUEST_CODE ;
        if( type == 1 )   // glucose
        {
            intent1.putExtra(LocalData.ALERT_TITLE_LABEL, "Glucose Test");
            intent1.putExtra(LocalData.ALERT_CONTENT_LABEL, "Glucose Testing Time is coming");

        } else
        {
            intent1.putExtra(LocalData.ALERT_TITLE_LABEL, "Insulin Injection");
            intent1.putExtra(LocalData.ALERT_CONTENT_LABEL, "Insulin Injection Time is coming");
            request_cd = 102;
        }
        Log.d(TAG, "SetAlert " + type + "   " + request_cd );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, request_cd, intent1, PendingIntent.FLAG_UPDATE_CURRENT );
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //  am.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES , pendingIntent);

    }

    public static void cancelReminder(Context context,Class<?> cls)
    {
        // Disable a receiver

        Log.d(TAG, "CancelReminder " ) ;
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void showNotification(Context context,Class<?> cls,String title,String content, int channelnbr )
    {
        Intent intent = new Intent(context , cls);//**The activity that you want to open when the notification is clicked
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context , channelnbr/* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID + channelnbr);

        if  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID + channelnbr, title, importance);

            channel.setDescription(content);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSmallIcon(R.drawable.ic_reminder)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

           notificationManager.createNotificationChannel(channel);

           notificationManager.notify(channelnbr, builder.build());
           Log.i(TAG , "send1 info " + Build.VERSION.SDK_INT  + "  " + Build.VERSION_CODES.O);

        } else
        {
           builder.setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_reminder)
                    .setPriority(importance);

            int PROGRESS_MAX = 100;
            int PROGRESS_CURRENT = 0;
            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            notificationManager.notify(channelnbr, builder.build());
            Log.i(TAG , "send2 " + Build.VERSION.SDK_INT  + "  " + Build.VERSION_CODES.O);
        }
    }

}