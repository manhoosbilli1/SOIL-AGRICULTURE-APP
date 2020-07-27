package com.example.soilagricultureiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.tv.TvView;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.security.Guard;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.nitri.gauge.Gauge;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.view.LineChartView;
import java.util.Calendar;
public class GasActivity extends AppCompatActivity {

    private static final String TAG = "values";
    ArrayList<Object> objectArrayList;
    private  static final int limit = 84600;
    public static boolean remakeList = false;
    private  ValueEventListener mListener;

    FirebaseDatabase mRoot;
    DatabaseReference mRootRef;
    LineDataSet lineDataSet = new LineDataSet(null,null);
    LineData lineData;
    LineChart chart;
    Gauge gauge;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);

        Date currenTime =Calendar.getInstance().getTime();
        Log.v(TAG, currenTime.toString());

        Button btnBack = findViewById(R.id.btnBack);
        chart = (LineChart) findViewById(R.id.chart);

        gauge = (Gauge) findViewById(R.id.gauge);
        mRoot = FirebaseDatabase.getInstance();
        mRootRef = mRoot.getReference();

        //chart settings
        chart.setNoDataText("No data to show, Failed to load from database.");
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawBorders(true);
        chart.setBorderColor(Color.BLACK);






        //below event listener is for updating the gauges only
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Float> dataMap = (Map)snapshot.getValue();
                assert dataMap != null;
                Number gasN = (Number) dataMap.get("gas");
                assert gasN != null;
                float gas = gasN.floatValue();
                String gasS = gasN.toString();
                gauge.moveToValue(gas);
                gauge.setLowerText(gasS);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GasActivity.this,"Failed to load data from database",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mRootRef.child("firebaseIOT").addValueEventListener(mListener);


        //adding another event listener for updating the graphs node: /soil-agriculture-iot/History/sensor;
        //this is reading the history and transferring it to an array



        mRootRef.child("History/gas").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Entry> entries = new ArrayList<>();
                float counter=0f;
                float counterData = 0f;

                for(DataSnapshot mySnapShot : snapshot.getChildren()){
                    Float yValue =  mySnapShot.getValue(Float.class);
                    counter += 1.0f;
                    entries.add(new Entry(counter, yValue));
                    counterData = counter++;

                }
                if(snapshot.getChildrenCount() >= limit){
                    mRootRef.child(("History/gas")).removeValue();   //cleans the data when its reached its limit
                }
                lineDataSet.setValues(entries);
                lineDataSet.setLabel("Gas Values ");
                lineDataSet.setColor(Color.WHITE);
                lineData = new LineData(lineDataSet);
                chart.clear();
                chart.setData((lineData));
                chart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GasActivity.this,"Failed to load data from database",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //show the retrieved array on a graph

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GasActivity.this, AdvancedActivity.class);
                startActivity(i);
                Toast.makeText(GasActivity.this, "Going back to menu", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRootRef.child("firebaseIOT").removeEventListener(mListener);
    }
}