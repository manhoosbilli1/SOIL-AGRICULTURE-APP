package com.example.soilagricultureiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.nitri.gauge.Gauge;

public class MoistureActivity extends AppCompatActivity {
    private static final String TAG = "values";
    private  static final int limit = 84600;
    FirebaseDatabase mRoot;
    DatabaseReference mRootRef;
    LineDataSet lineDataSet = new LineDataSet(null,null);
    LineData lineData;
    LineChart chart;
    Gauge gauge;
    private ValueEventListener mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moisture);

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


        // listener for moisture to update gauge
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Float> dataMap = (Map)snapshot.getValue();
                Number moistureN = (Number) dataMap.get("moisture");
                assert moistureN != null;
                float moisture = moistureN.floatValue();
                String moistureS = moistureN.toString();
                gauge.moveToValue(moisture);
                gauge.setLowerText(moistureS);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MoistureActivity.this,"Failed to load data from database",
                        Toast.LENGTH_SHORT).show();            }
        };
        mRootRef.child("firebaseIOT").addValueEventListener(mListener);


        //listener for moisture to update the chart.

        mRootRef.child("History/moisture").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
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
                    mRootRef.child(("History/moisture")).removeValue();   //cleans the data when its reached its limit
                }
                lineDataSet.setValues(entries);
                lineDataSet.setLabel("Moisture ");
                lineData = new LineData(lineDataSet);
                chart.clear();
                chart.setData((lineData));
                chart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MoistureActivity.this,"Failed to load data from database",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MoistureActivity.this, AdvancedActivity.class);
                startActivity(i);
                Toast.makeText(MoistureActivity.this, "Going back to menu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRootRef.child("firebaseIOT").removeEventListener(mListener);
    }
}