package com.lantu.mychart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.service.autofill.UserData;
import android.support.v4.app.AppLaunchChecker;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EditActivity extends AppCompatActivity {

    static final String TAG = "editActivity";
    Button  btnupdateinsulin;
    TextView tvTitle, tvPredictDose;

    EditText etGlucose, etTestDate, etInsuline;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.input_icon);
        actionBar.setTitle(Html.fromHtml("<font color='red'> Add/Edit</font>"));

        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
         extras = getIntent().getExtras();
        int position =0;

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String cdate = f.format(new Date() );

   //     btnSubmit = findViewById(R.id.btnSubmit);
        tvTitle = findViewById(R.id.tvTitle);
        etGlucose = findViewById(R.id.etGlucose);
        etInsuline= findViewById(R.id.etInsuline);
        etTestDate = findViewById(R.id.etTestDate);
        tvPredictDose = findViewById(R.id.tvPredictDose);
        btnupdateinsulin = findViewById(R.id.btupdateinsulin);

        if (extras != null)
        {
            position = extras.getInt("myobject");
            ApplicationClass.setPosition(position);
            tvTitle.setText(getString(R.string.editpageTitle));
            userDaily cdata = (userDaily) ApplicationClass.list.get(position);
            etTestDate.setText( cdata.getTestDate().toString() );
            etGlucose.setText(String.valueOf(cdata.getGlucoseDose()));
            etInsuline.setText( String.valueOf( cdata.getInsulinDose()));
            tvPredictDose.setText(getString(R.string.predictedinsulin) + " :" + String.valueOf(cdata.getInsulinPredict()));

        }


    }


    public void updateClick(View view) {
        if  (extras != null)
        {

            if ( etTestDate.getText() != null && etInsuline.getText() != null && etGlucose.getText() != null ) {
                String tdate = etTestDate.getText().toString().trim();
                double tinsulindose = Double.valueOf(etInsuline.getText().toString().trim());
                double tglucose = Double.valueOf(etGlucose.getText().toString().trim());

                if ( tglucose > ApplicationClass.glucose_limit )
                {
                    ApplicationClass.Msg(getString(R.string.glucose_out_of_range), EditActivity.this    );
                    return;
                }

                if (tinsulindose > ApplicationClass.insulin_limit )
                {
                    ApplicationClass.Msg(getString(R.string.insulin_out_of_range), EditActivity.this    );
                    return;
                }
                userDaily data = ApplicationClass.list.get(ApplicationClass.position);
                data.setGlucoseDose(tglucose);
                data.setInsulinDose(tinsulindose);
                data.setTestDate(tdate);
                ApplicationClass.list.set(ApplicationClass.position, data);

                doupdatepost(tdate, tglucose,tinsulindose, 0);
                // Toast.makeText(EditActivity.this, "Please fill all of the fields", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void doupdatepost(String testdate, double glucose, double insulin , double predictinsulin)
    {
        try {
            Map<String, Object> loginMap = new HashMap<String, Object>();
            loginMap.put("url","http://" + ApplicationClass.httpurl + "/updaterecord");
            Log.i(TAG, loginMap.get("url").toString());
            loginMap.put("testdate", testdate);
            loginMap.put("glucose", glucose);
            loginMap.put("insulin", insulin) ;
            loginMap.put("data", new Object());
            newdataPost updatedatapost = new newdataPost();
            String ret = updatedatapost.execute(loginMap).get();

        } catch (InterruptedException ex) {
            Toast.makeText( this, "Exception  " +  ex.getMessage(), Toast.LENGTH_SHORT).show(); ;
        } catch (ExecutionException ec)
        {
            Toast.makeText( this, "Exception  " +  ec.getMessage(), Toast.LENGTH_SHORT).show(); ;
        }

    }


    public class newdataPost extends AsyncTask <Map, Void, String >
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

            tvPredictDose.setText( getString(R.string.predictedinsulin) + "  " +  s);
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
                jobj.put("id", ApplicationClass.list.get(ApplicationClass.position).getId());

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
                        ud.setGlucoseDose(glucosemorning);
                        ud.setInsulinDose(insulindoseevening + insulindosemorning);
                        ud.setInsulinPredict(suggestedinsulin);

                        ApplicationClass.list.add(ud);

                        if ( i ==  0 )
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


}
