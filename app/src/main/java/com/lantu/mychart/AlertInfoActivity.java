package com.lantu.mychart;

import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.ClipboardManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlertInfoActivity extends AppCompatActivity {

    String TAG = "AlertInfoActivity";
    LocalData localData;

    SwitchCompat reminderSwitch;
    TextView tvTime , tvTime2;

    LinearLayout ll_set_time, ll_set_time2;
    int hour, min;

    ClipboardManager myClipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_info);
        localData = new LocalData(getApplicationContext());

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ll_set_time = (LinearLayout) findViewById(R.id.ll_set_time);
        ll_set_time2 = (LinearLayout) findViewById(R.id.ll_set_time2);
        //    ll_terms = (LinearLayout) findViewById(R.id.ll_terms);

        tvTime = (TextView) findViewById(R.id.tv_reminder_time_desc);
        tvTime2 = (TextView)findViewById(R.id.tv_reminder_time_desc2);

        reminderSwitch = (SwitchCompat) findViewById(R.id.timerSwitch);

        hour = localData.get_hour();
        min = localData.get_min();

        tvTime.setText(getFormatedTime(hour, min));
        tvTime2.setText(getFormatedTime(localData.get_ihour() , localData.get_imin()));

        reminderSwitch.setChecked(localData.getReminderStatus());

        if (!localData.getReminderStatus())
            ll_set_time.setAlpha(0.4f);

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                localData.setReminderStatus(isChecked);
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: true");
                //    NotificationScheduler.setReminder(AlertInfoActivity.this  , AlarmReceiver.class, localData.get_hour(), localData.get_min() , 1);
                    ll_set_time.setAlpha(1f);
               //     NotificationScheduler.setReminder(AlertInfoActivity.this, AlarmReceiver.class, localData.get_ihour(), localData.get_imin(), 2);
                    NotificationScheduler.setReminders(AlertInfoActivity.this, AlarmReceiver.class);
                    ll_set_time2.setAlpha(1f);
                } else {
                    Log.d(TAG, "onCheckedChanged: false");
                    NotificationScheduler.cancelReminder(AlertInfoActivity.this, AlarmReceiver.class);
                    ll_set_time.setAlpha(0.4f);
                    ll_set_time2.setAlpha(0.4f);
                }

            }
        });

        ll_set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (localData.getReminderStatus())
                    showTimePickerDialog(localData.get_hour(), localData.get_min());
            }
        });

        ll_set_time2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (localData.getReminderStatus())
                    showTimePickerDialog2(localData.get_ihour(), localData.get_imin());
            }
        });




    }


    private void showTimePickerDialog2(int h, int m) {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.timepicker_header, null);

        TimePickerDialog builder = new TimePickerDialog(this, R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        Log.d(TAG, "onTimeSet: ihour " + hour);
                        Log.d(TAG, "onTimeSet: imin " + min);
                        localData.set_ihour(hour);
                        localData.set_imin(min);
                        tvTime2.setText(getFormatedTime(hour, min));
                       // NotificationScheduler.setReminder(AlertInfoActivity.this, AlarmReceiver.class, localData.get_ihour(), localData.get_imin(), 2);
                        NotificationScheduler.setReminders(AlertInfoActivity.this, AlarmReceiver.class);

                    }
                }, h, m, false);

        builder.setCustomTitle(view);
        builder.show();

    }
    private void showTimePickerDialog(int h, int m) {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.timepicker_header, null);

        TimePickerDialog builder = new TimePickerDialog(this, R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        Log.d(TAG, "onTimeSet: hour " + hour);
                        Log.d(TAG, "onTimeSet: min " + min);
                            localData.set_hour(hour);
                            localData.set_min(min);
                            tvTime.setText(getFormatedTime(hour, min));
                         //   NotificationScheduler.setReminder(AlertInfoActivity.this, AlarmReceiver.class, localData.get_hour(), localData.get_min(), 1);
                            NotificationScheduler.setReminders(AlertInfoActivity.this, AlarmReceiver.class);

                    }
                }, h, m, false);

        builder.setCustomTitle(view);
        builder.show();

    }

    public String getFormatedTime(int h, int m) {
        final String OLD_FORMAT = "HH:mm";
        final String NEW_FORMAT = "hh:mm a";

        String oldDateString = h + ":" + m;
        String newDateString = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT, getCurrentLocale());
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newDateString;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }
}