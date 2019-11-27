package com.lantu.mychart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class userDailyAdapter extends ArrayAdapter <userDaily> {
    private final Context context;
    private final ArrayList<userDaily> values;

    public userDailyAdapter(Context context, ArrayList<userDaily> list) {
        super(context, R.layout.row_layout, list);
        this.context = context;
        this.values = list;
    }

    @NonNull
    @Override
    public View getView(int position,View convertView,  ViewGroup parent) {
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View rowview = inflater.inflate(R.layout.row_layout, parent, false);
        TextView  tvDate, tvGlucose, tvInsuline, tvInsulinePredicted;
        tvDate = rowview.findViewById(R.id.tvDate);
        tvGlucose= rowview.findViewById(R.id.tvGlucose );
        tvInsuline = rowview.findViewById(R.id.tvInsulinDose);
        tvInsulinePredicted= rowview.findViewById(R.id.tvInsulinPredicted);

        tvDate.setText(values.get(position).getTestDate());
        tvGlucose.setText("Glucose " + values.get(position).getGlucoseDose());
        tvInsuline.setText("Insulin Dose " + values.get(position).getInsulinDose());
        tvInsulinePredicted.setText("Suggested Dose " + values.get(position).getInsulinPredict());

        return rowview;
    }
}
