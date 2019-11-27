package com.lantu.mychart;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.AppLaunchChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class setinfoActivity extends AppCompatActivity implements RestFulResult{

    Button btnsetinfo;
    EditText etuserfname, etuserlname,etuserphone ;
    String userID="";
    final String TAG="setinfoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setinfo);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.input_icon);
        actionBar.setTitle(Html.fromHtml("<font color='red'> " + getString(R.string.app_name) +" </font>"));

        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnsetinfo = findViewById(R.id.btnuserinfo);
        etuserfname = findViewById(R.id.etUserfName);
        etuserlname = findViewById(R.id.etUserLName);
        etuserphone = findViewById(R.id.etUserPhone);

        if (ApplicationClass.userfname != null )
        {
            etuserfname.setText(ApplicationClass.userfname);
        }
        if (ApplicationClass.userlname != null )
        {
            etuserlname.setText(ApplicationClass.userlname);
        }
        if (ApplicationClass.userTel != null )
        {
            etuserphone.setText(ApplicationClass.userTel );
        }

        btnsetinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( etuserfname.getText() != null && etuserphone.getText() != null && etuserlname.getText() != null  )
                {
                    String fname =  etuserfname.getText().toString().trim();
                    String lname = etuserlname.getText().toString().trim();
                    String phone = etuserphone .getText().toString().trim();
  //                   if ( ApplicationClass.userTel == null || ApplicationClass.userTel.isEmpty()  ) {


                             userID = dopost(fname, lname, phone);
                             Log.i(TAG, "UserID is " + userID );
                             if (!userID.isEmpty()) {
                                 SharedPreferences.Editor editor = getSharedPreferences(ApplicationClass.MY_PREFS_FILENAME, MODE_PRIVATE).edit();
                                 editor.clear();
                                 editor.putString(ApplicationClass.FNAMELABEL, fname);
                                 editor.putString(ApplicationClass.LNAMELABEL, lname);
                                 editor.putString(ApplicationClass.USERPHONELABEL, phone);
                                 editor.putString(ApplicationClass.USERIDLABEL, userID);
                                 editor.commit();

                                 if ( ApplicationClass.cdata.getUserID().compareToIgnoreCase(userID) !=0  )
                                 {
                                     ApplicationClass.list.clear();
                                     ApplicationClass.userID = userID;
                                     ApplicationClass.userTel = phone ;
                                     ApplicationClass.userfname = fname;
                                     ApplicationClass.userlname  = lname;
                                     refreshdata();

                                 }

                                 Toast.makeText(setinfoActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                             }


   //                  }

                } else {
                    Toast.makeText(setinfoActivity.this, "Please fill up the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


    public void refreshdata( )
    {
        try {
            Map<String, Object> loginMap = new HashMap<String, Object>();
            loginMap.put("url","http://" + ApplicationClass.httpurl +"/getdatabyuserid");
            loginMap.put("userid", ApplicationClass.userID);
            loginMap.put(ApplicationClass.USERPHONELABEL , ApplicationClass.userTel);
            loginMap.put("data", new Object());
            RestPost restFulPost = new RestPost(this, setinfoActivity.this , "Please Wait", "get data");
            String ret = restFulPost.execute(loginMap).get();
        } catch (InterruptedException ex) {
            Toast.makeText( this, "Exception  " +  ex.getMessage(), Toast.LENGTH_SHORT).show(); ;
        } catch (ExecutionException ec)
        {
            Toast.makeText( this, "Exception  " +  ec.getMessage(), Toast.LENGTH_SHORT).show(); ;
        }

    }

    public String dopost(String fname, String lname, String userTel )
    {
        String retid = "" ;
        try {
            Map<String, Object> loginMap = new HashMap<String, Object>();
            loginMap.put("url", "http://" + ApplicationClass.httpurl + "/getuserid");
            Log.i(TAG, loginMap.get("url").toString());
            loginMap.put("fname", fname);
            loginMap.put("lname", lname);
            loginMap.put("phone", userTel);
            loginMap.put("data", new Object());
            userIDPost useridpost = new userIDPost();
            retid = useridpost.execute(loginMap).get();
        } catch (InterruptedException ex) {
            ex.getMessage();
        } catch (ExecutionException ec)
        {
            ec.getMessage();
        }

        return retid;
        //tvPredictDose.setText( String.valueOf(ApplicationClass.cdata.getInsulinPredict()));
    }


    public class userIDPost extends AsyncTask<Map, Void, String >
    {
        public userIDPost() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            userID = s;
        }

        @Override
        protected String doInBackground(Map... maps) {

            StringBuffer sb = new StringBuffer();
            Object dataMap = null;
            String ret ="";
            try {
                URL url = new URL(maps[0].get("url").toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject jobj = new JSONObject();

                jobj.put("fname", maps[0].get("fname").toString());
                jobj.put("lname", maps[0].get("lname").toString());
                jobj.put("phone", maps[0].get("phone").toString());

                //           String input = "{\"pid\":7, \"testdate\":\"" +  + "\" }";

                OutputStream os = conn.getOutputStream();
                os.write(jobj.toString().getBytes());
                os.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;


                //
                //         System.out.println("Output from Server .... \n");

                while ((output = br.readLine()) != null) {
                    sb.append(output);
                    //           System.out.println(output);
                }
                Log.i( "Call " , sb.toString());
                try {
                    System.out.println(sb.toString());
                    int indx = sb.indexOf("{");
                    //               System.out.println(indx + "   " + sb.length());

                    JSONObject jObject = new JSONObject(sb.toString().substring(indx));
                    JSONObject records = (JSONObject) jObject.get("users");

                    boolean error = jObject.getBoolean("error");

                    if ( records.get("id") != null ) {
                        int id = records.getInt("id");

                        ret = String.valueOf(id);

//                    list
                        Log.i("setinfoActivity", "return value " + ret + "  ");
                    }

                } catch (JSONException je) {
                    je.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;

        }
    }


    @Override
    public void onResfulResponse(String result, String responseFor) {
        return  ;
    }
}
