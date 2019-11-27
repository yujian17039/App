package com.lantu.mychart;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class RestPost extends AsyncTask <Map, Void, String>{
    RestFulResult restFulResult = null;
    ProgressDialog Asycdialog;
    String msg;
    String task;

    public RestPost(RestFulResult restFulResult, Context context, String msg, String task) {
        this.restFulResult = restFulResult;
        this.task=task;
        this.msg = msg;
        Asycdialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        Asycdialog.setMessage(msg);
        //show dialog
        Asycdialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        Asycdialog.dismiss();
        restFulResult.onResfulResponse(s,task);

        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(Map... maps) {
        StringBuffer sb = new StringBuffer();
        Object dataMap = null;

        try {
            URL url = new URL(maps[0].get("url").toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject jobj = new JSONObject();

            jobj.put("userid", maps[0].get("userid").toString());
            jobj.put("phone", maps[0].get(ApplicationClass.USERPHONELABEL).toString());
    //        jobj.put("phone", maps[0].get("phone").toString());


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
                ApplicationClass.list.clear();
                JSONObject jObject = new JSONObject(sb.toString().substring(indx));
                JSONArray records = (JSONArray) jObject.get("users");
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
//                    list.add(ud);
                    if ( i ==0  )
                    {
                        ApplicationClass.setcdata( ud );
                        //ret = String.valueOf(suggestedinsulin);
                        Log.i("RestPost", "set current data i " + ApplicationClass.cdata.getTestDate() + "  " + records.length());
                    }


                }

            } catch (JSONException je) {
                je.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();

    }
}
