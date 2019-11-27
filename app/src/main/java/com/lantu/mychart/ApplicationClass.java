package com.lantu.mychart;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AsyncPlayer;
import android.support.v7.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ApplicationClass extends Application {

    public static ArrayList<userDaily> list ;
    public static String userID = "";
    public static String userfname="";
    public static String userlname="";
    public static String userTel ="" ;
    public static int position ;
    public static userDaily cdata;
    public static String httpurl ;

    public static final double glucose_limit =50.0;
    public static final double insulin_limit =150.0;

    public static final String MY_PREFS_FILENAME="com.lantu.mychart.Names";
    public static final String FNAMELABEL = "userfname";
    public static final String LNAMELABEL = "userlname";
    public static final String USERIDLABEL = "userID";
    public static final String USERPHONELABEL = "userphone";

    public static int getPosition() {
        return position;
    }

    public static void setPosition(int position) {
        ApplicationClass.position = position;
    }

    @Override

    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationClass.cdata=new userDaily();

        list = new ArrayList<userDaily>();
        ApplicationClass.httpurl = getString(R.string.httphost) +"/" + getString(R.string.httpservice);

        SharedPreferences editor1 = getSharedPreferences(MY_PREFS_FILENAME, MODE_PRIVATE);
        String fname = editor1.getString(FNAMELABEL, null);
        String lname = editor1.getString(LNAMELABEL, null);
        String pid = editor1.getString(USERIDLABEL, null);
        String phone = editor1.getString(USERPHONELABEL , null);

        ApplicationClass.userID = pid;
        ApplicationClass.userfname = fname;
        ApplicationClass.userlname = lname;
        ApplicationClass.userTel = phone;
    }

    public static void setcdata(userDaily ud )
    {
        ApplicationClass.cdata =  ud;
    }


    public static void Msg(String msg, Context context)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context  ).create();
        alertDialog.setTitle(R.string.alerttitle);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }
}
