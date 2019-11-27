package com.lantu.mychart;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements RestFulResult {

    ListView lvDailyDoses ;
    userDailyAdapter myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.input_icon);
        actionBar.setTitle(Html.fromHtml("<font color='red'> " + getString(R.string.app_name) + " </font>"));

        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        lvDailyDoses = (ListView) findViewById(R.id.lvDailyDose);

         myadapter = new userDailyAdapter(this, ApplicationClass.list );


        lvDailyDoses.setAdapter(myadapter);

        lvDailyDoses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userDaily item =(userDaily) parent.getItemAtPosition(position);
              //  Toast.makeText( parent , item.getTestDate() , Toast.LENGTH_SHORT ) .show();
                Intent intent = new Intent( MainActivity.this , EditActivity.class );
                intent.putExtra("myobject", position);
                startActivity(intent);
 //               gett();
            }
        });

        if (ApplicationClass.list.size() < 1) {
            getPost();
        }
    }

    @NonNull
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {

            case R.id.refresh:
  //              ApplicationClass.list.clear();
                getPost();
                myadapter.notifyDataSetChanged();
                break;
            case R.id.addNew:
                Intent intent = new Intent( MainActivity.this , AddActivity.class );
                startActivity(intent);
                break;
            case R.id.viewChart:
                Intent chartintent = new Intent( MainActivity.this , chartActivity.class );
                startActivity(chartintent);
                break;
            case R.id.myInfo:
               Intent infointent = new Intent( MainActivity.this , setinfoActivity.class );
                startActivity(infointent);
                break;
            case R.id.alert:
                Intent alertintent = new Intent( MainActivity.this , AlertInfoActivity.class );
                startActivity(alertintent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getPost()
    {
        try {
            Map<String, Object> loginMap = new HashMap<String, Object>();
            loginMap.put("url","http://" + ApplicationClass.httpurl +"/getdatabyuserid");
                 loginMap.put("userid", ApplicationClass.userID);
                 loginMap.put(ApplicationClass.USERPHONELABEL , ApplicationClass.userTel);
            loginMap.put("data", new Object());
            RestPost restFulPost = new RestPost(this, MainActivity.this , "Please Wait", "get data");
            String ret = restFulPost.execute(loginMap).get();
        } catch (InterruptedException ex) {
            Toast.makeText( this, "Exception  " +  ex.getMessage(), Toast.LENGTH_SHORT).show(); ;
        } catch (ExecutionException ec)
        {
            Toast.makeText( this, "Exception  " +  ec.getMessage(), Toast.LENGTH_SHORT).show(); ;
        }

}

    @Override
    public void onResfulResponse(String result, String responseFor) {
        return  ;
    }
}
