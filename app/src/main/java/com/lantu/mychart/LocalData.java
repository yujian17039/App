package com.lantu.mychart;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalData {

    private static final String APP_SHARED_PREFS = "com.lantu.mychart.PmmcAlert";

    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    private static final String reminderStatus="reminderStatus";
    private static final String hour="hour";
    private static final String min="min";

    private static final String ihour="ihour";
    private static final String imin="imin";

    public static final String ALERT_TITLE_LABEL="Title";
    public static final String ALERT_CONTENT_LABEL="Content";

    public LocalData(Context context)
    {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    // Settings Page Set Reminder

    public boolean getReminderStatus()
    {
        return appSharedPrefs.getBoolean(reminderStatus, false);
    }

    public void setReminderStatus(boolean status)
    {
        prefsEditor.putBoolean(reminderStatus, status);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Hour)

    public int get_hour()
    {
        return appSharedPrefs.getInt(hour, 20);
    }

    public void set_hour(int h)
    {
        prefsEditor.putInt(hour, h);
        prefsEditor.commit();
    }


    public int get_ihour()
    {
        return appSharedPrefs.getInt(ihour, 20);
    }

    public void set_ihour(int h)
    {
        prefsEditor.putInt(ihour, h);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Minutes)

    public int get_imin()
    {
        return appSharedPrefs.getInt(imin, 0);
    }

    public void set_imin(int m)
    {
        prefsEditor.putInt(imin, m);
        prefsEditor.commit();
    }

    public int get_min()
    {
        return appSharedPrefs.getInt(min, 0);
    }

    public void set_min(int m)
    {
        prefsEditor.putInt(min, m);
        prefsEditor.commit();
    }

    public void reset()
    {
        prefsEditor.clear();
        prefsEditor.commit();

    }

}