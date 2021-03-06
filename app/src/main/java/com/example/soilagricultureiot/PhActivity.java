package com.example.soilagricultureiot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
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
import android.os.Bundle;

public class PhActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_ph);
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


        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Float> dataMap = (Map)snapshot.getValue();
                Number phN = (Number) dataMap.get("ph");
                assert phN != null;
                float ph = phN.floatValue();
                String phS = phN.toString();
                gauge.moveToValue(ph);
                gauge.setLowerText(phS);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PhActivity.this,"Failed to load data from database",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mRootRef.child("firebaseIOT").addValueEventListener(mListener);

        //listener for Ph to update the chart.

        mRootRef.child("History/ph").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
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
                    mRootRef.child(("History/ph")).removeValue();   //cleans the data when its reached its limit
                }
                lineDataSet.setValues(entries);
                lineDataSet.setLabel("PH Values ");
                lineData = new LineData(lineDataSet);
                chart.clear();
                chart.setData((lineData));
                chart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PhActivity.this,"Failed to load data from database",
                        Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PhActivity.this, AdvancedActivity.class);
                startActivity(i);
                Toast.makeText(PhActivity.this, "Going back to menu", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRootRef.child("firebaseIOT").removeEventListener(mListener);
    }
}