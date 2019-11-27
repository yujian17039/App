package com.lantu.mychart;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import android.graphics.Color;
import android.text.Html;

import java.util.ArrayList;

public class chartActivity extends AppCompatActivity {

    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.input_icon);
        actionBar.setTitle(Html.fromHtml("<font color='red'> " + getString(R.string.app_name) +" </font>"));

        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        lineChart = (LineChart) findViewById(R.id.lineChart);

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> entry = new ArrayList<>();
        int count=0;
        for(int i=ApplicationClass.list.size()-1; i>=0 ; i-- )
        {
            userDaily ud = ApplicationClass.list.get(i);
            entries.add(new Entry((float) count , (float)ud.getGlucoseDose() ));
            entry.add(new Entry((float) count, (float)ud.getInsulinDose() ));
            count++;
        }


        ArrayList<ILineDataSet> lines = new ArrayList<> ();
        String[] xAxis = new String[] {"1", "2", "3", "4", "5","6"};
        LineDataSet lDataSet1 = new LineDataSet(entries, "Glucose");
        lDataSet1.setDrawFilled(true);
        lDataSet1.setColor(Color.BLUE);
        lines.add(lDataSet1);

        LineDataSet lDataSet2 = new LineDataSet(entry, "Insulin");
        lDataSet2.setDrawFilled(true);
        lDataSet2.setColor(Color.RED);
        lines.add(lDataSet2);

        lineChart.setData(new LineData(lines));
        lineChart.animateY(5000);
    }
}
