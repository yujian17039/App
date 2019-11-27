package com.lantu.mychart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AddActivity extends AppCompatActivity {

    static final String TAG = "AddActivity";
    Button btnglucoseSubmit, btnupdateinsulin;
    TextView tvTitle, tvPredictDose;

    EditText etGlucose, etTestDate, etInsulin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.input_icon);
        actionBar.setTitle(Html.fromHtml("<font color='red'> " + getString(R.string.app_name) +" </font>"));

        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        int position =0;

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String cdate = f.format(new Date() );

        etGlucose = findViewById(R.id.etGlucoseDose);
        etInsulin= findViewById(R.id.etInsulinDose);
        etTestDate = findViewById(R.id.etTestingDate);
        tvPredictDose = findViewById(R.id.tvPredictedInsulin);

        btnglucoseSubmit = findViewById(R.id.btnGlucoseSubmit);
        btnupdateinsulin= findViewById(R.id.btnInsulinSubmit);

        etTestDate.setText(cdate);

        boolean findinfo = false;
        double tmp  = ApplicationClass.cdata.getInsulinPredict();
        Log.d(TAG, "onCreate: " + tmp +    "   " + cdate);
        if (tmp > 0.1 &&  ApplicationClass.cdata.getTestDate().equalsIgnoreCase(cdate))
        {
            findinfo = true;
        }

        if (findinfo)
        {
            showSecond();
            tvPredictDose.setText(getString(R.string.predictedinsulin) + " " + String.valueOf(ApplicationClass.cdata.getInsulinPredict()));
            etGlucose.setText(String.valueOf(ApplicationClass.cdata.getGlucoseDose()));
            etInsulin.setText(String.valueOf(ApplicationClass.cdata.getInsulinDose()));

        } else
        {
            showFirst();
        }


        btnglucoseSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etTestDate.getText() != null && etGlucose.getText() != null) {
                    String tdate = etTestDate.getText().toString().trim();
                    //       double tinsulindose = Double.valueOf(etInsuline.getText().toString().trim());
                    double tglucose = Double.valueOf(etGlucose.getText().toString().trim());

                    if ( tglucose >  ApplicationClass.glucose_limit ) {

                        ApplicationClass.Msg(getString(R.string.glucose_out_of_range) , AddActivity.this); ;
                        return;

                    }

                    userDaily newUD = new userDaily();
                    newUD.setTestDate(tdate);
                    newUD.setGlucoseDose(tglucose);
                    ApplicationClass.cdata = newUD;
                    // get calculation result
                   String prdDose = dopost(tdate, tglucose, 0);

                    //String prdDose = tvPredictDose.getText().toString().trim();
                    if (prdDose.length() >0 ) {
                        showSecond();

                        Toast.makeText(AddActivity.this, "Add successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnupdateinsulin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tdate = etTestDate.getText().toString().trim();
                double tinsulindose = Double.valueOf(etInsulin.getText().toString().trim());
                double tglucose = Double.valueOf(etGlucose.getText().toString().trim());

                // get calculation result

                if ( tinsulindose > ApplicationClass.insulin_limit )
                {
                    ApplicationClass.Msg(getString(R.string.insulin_out_of_range) , AddActivity.this); ;
                    return;
                }

                doupdatepost(tdate, tglucose, tinsulindose, ApplicationClass.cdata.getInsulinPredict() );

                ApplicationClass.cdata.setGlucoseDose(tglucose);
               // ApplicationClass.list.get(0).setGlucoseDose(tglucose);
                ApplicationClass.list.get(0).setInsulinDose(tinsulindose);
                Intent intent = new Intent(AddActivity.this , MainActivity.class);
                startActivity(intent);

                Toast.makeText(AddActivity.this, "Update successfully", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void showSecond()
    {
        FragmentManager fg= AddActivity.this.getSupportFragmentManager();
        fg.beginTransaction()
                .hide(fg.findFragmentById(R.id.frgGlucose))
                .show(fg.findFragmentById(R.id.frgInsulin))
                .commit();

    }

    public void showFirst()
    {
        FragmentManager fg= AddActivity.this.getSupportFragmentManager();
        fg.beginTransaction()
                .show(fg.findFragmentById(R.id.frgGlucose))
                .hide(fg.findFragmentById(R.id.frgInsulin))
                .commit();

    }


    public void doupdatepost(String testdate, double glucose, double insulin , double predictinsulin)
    {

      try {
        Map<String, Object> loginMap = new HashMap<String, Object>();
        loginMap.put("url","http://" + ApplicationClass.httpurl + "/updatenewrecord");
        Log.i(TAG, loginMap.get("url").toString());
        loginMap.put("testdate", testdate);
        loginMap.put("glucose", glucose);
        loginMap.put("insulin", insulin) ;
        loginMap.put("data", new Object());
        AddActivity.updatedataPost updatedatapost = new AddActivity.updatedataPost();
        String ret = updatedatapost.execute(loginMap).get() ;
    } catch (InterruptedException ex) {
        Toast.makeText( this, "Exception  " +  ex.getMessage(), Toast.LENGTH_SHORT).show(); ;
    } catch (ExecutionException ec)
    {
        Toast.makeText( this, "Exception  " +  ec.getMessage(), Toast.LENGTH_SHORT).show(); ;
    }


    }

    String dopost(String testdate, double glucose, double insulin)
        {
            String ret = "";

            try {
            Map<String, Object> loginMap = new HashMap<String, Object>();
            loginMap.put("url","http://" + ApplicationClass.httpurl + "/addnewrecord");
            Log.i(TAG, loginMap.get("url").toString());
            loginMap.put("testdate", testdate);
            loginMap.put("glucose", glucose);
            loginMap.put("insulin", insulin) ;
            loginMap.put("data", new Object());
            AddActivity.newdataPost newdataPost = new AddActivity.newdataPost();
            ret = newdataPost.execute(loginMap).get();
            //tvPredictDose.setText( String.valueOf(ApplicationClass.cdata.getInsulinPredict()));
            } catch (InterruptedException ex) {
                Toast.makeText( this, "Exception  " +  ex.getMessage(), Toast.LENGTH_SHORT).show(); ;
            } catch (ExecutionException ec)
            {
                Toast.makeText( this, "Exception  " +  ec.getMessage(), Toast.LENGTH_SHORT).show(); ;
            }
            return ret ;
        }

        class newdataPost extends AsyncTask<Map, Void, String >
        {
            public newdataPost() {
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                tvPredictDose.setText( "Predicted Dose " +  s);
                ApplicationClass.cdata.setInsulinPredict(Double.valueOf(s));
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

                    jobj.put("testdate", maps[0].get("testdate").toString());
                    jobj.put("pid", Integer.valueOf( ApplicationClass.userID) );
                    jobj.put("glucose", maps[0].get("glucose"));
                    jobj.put("insulin", maps[0].get("insulin"));
                    jobj.put("id", ApplicationClass.cdata.getId());

                    //           String input = "{\"pid\":7, \"testdate\":\"" +  + "\" }";

                    Log.i(TAG, "return value " + jobj.toString() + "  "  );

                    OutputStream os = conn.getOutputStream();
                    os.write(jobj.toString().getBytes());
                    os.flush();

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

                    String output;

                    //         System.out.println("Output from Server .... \n");

                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                        //           System.out.println(output);
                    }


                    try {
                        System.out.println(sb.toString());
                        int indx = sb.indexOf("{");
                        //               System.out.println(indx + "   " + sb.length());

                        JSONObject jObject = new JSONObject(sb.toString().substring(indx));
                        JSONArray records = (JSONArray) jObject.get("data");
                        ApplicationClass.list.clear();

                        for (int i = 0; i < records.length(); i++) {
                            JSONObject record = (JSONObject) records.get(i);
                            int id = record.getInt("id");
                            String testdate = record.getString("testdate");
                            double glucosemorning = record.getDouble("glucosemorning");
                            double glucoseevening = record.getDouble("glucoseevening");
                            double insulindoseevening = record.getDouble("insulindoseevening");
                            double insulindosemorning = record.getDouble("insulindosemorning");
                            double suggestedinsulin = record.getDouble("suggestedinsulin");
                            //             System.out.println("testdate " + testdate + " id " + id );

                            if (glucoseevening > 0.1)
                            {
                                glucosemorning = (glucosemorning + glucoseevening)/2;
                            }

                            userDaily ud = new userDaily();
                            ud.setId(id);
                            ud.setTestDate(testdate);
                            ud.setGlucoseDose( glucosemorning);
                            ud.setInsulinDose(insulindoseevening + insulindosemorning);
                            ud.setInsulinDose(suggestedinsulin);

                            ApplicationClass.list.add(ud);

                            if ( i  ==  0 )
                            {
                                ApplicationClass.setcdata( ud );
                                ret = String.valueOf(suggestedinsulin);
                            }
//                    list
                            Log.i(TAG, "return value " + ret + "  " + i );

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

         class updatedataPost extends AsyncTask <Map, Void, String >
        {
            public updatedataPost() {
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                tvPredictDose.setText( "Predicted Dose " +  s);
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

                    jobj.put("testdate", maps[0].get("testdate").toString());
                    jobj.put("pid", ApplicationClass.userID);
                    jobj.put("glucose", maps[0].get("glucose"));
                    jobj.put("insulin", maps[0].get("insulin"));
                    jobj.put("id", ApplicationClass.cdata.getId());

                    //           String input = "{\"pid\":7, \"testdate\":\"" +  + "\" }";


                    OutputStream os = conn.getOutputStream();
                    os.write(jobj.toString().getBytes());
                    os.flush();

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

                    String output;

                    //         System.out.println("Output from Server .... \n");

                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                        //           System.out.println(output);
                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ret;

            }
        }

    }
