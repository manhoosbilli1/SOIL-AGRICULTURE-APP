package com.example.soilagricultureiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

public class AdvancedActivity extends AppCompatActivity {
    FirebaseDatabase mRoot;
    DatabaseReference mRootRef;
    private static Float latitude;
    private static Float longitude;
    private static Integer balance;
    private static final String TAG = "Advanced";
    Button btnBasic,btnPH,btnMoisture,btnLocation,btnHumidity,btnGas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);
        mRoot = FirebaseDatabase.getInstance();
        mRootRef = mRoot.getReference();
        Button btnBasic = (Button) findViewById(R.id.btnBasic);
        Button btnHumidity = (Button) findViewById(R.id.btnHumidity);
        Button btnGas = (Button) findViewById(R.id.btnGas);
        Button btnPH = (Button) findViewById(R.id.btnPH);
        Button btnMoisture = (Button) findViewById(R.id.btnMoisture);
        Button btnLocation = (Button) findViewById(R.id.btnMap);
        fetchCoordinates();


        btnBasic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(AdvancedActivity.this, BasicActivity.class);
                    startActivity(i);
                }
        });
        btnHumidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdvancedActivity.this, humidityActivity.class);
                startActivity(i);
            }
        });

        btnPH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdvancedActivity.this, PhActivity.class);
                startActivity(i);
            }
        });

        btnGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdvancedActivity.this, GasActivity.class);
                startActivity(i);
            }
        });
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdvancedActivity.this, MapsActivity.class);
                Bundle mBundle = new Bundle();
                if(AdvancedActivity.latitude != null) {
                    mBundle.putFloat("latitude", AdvancedActivity.latitude);
                    mBundle.putFloat("longitude", AdvancedActivity.longitude);
                    mBundle.putInt("balance", AdvancedActivity.balance);
                    i.putExtras(mBundle);
                    startActivity(i);
                }else{
                    mBundle.putInt("balance", AdvancedActivity.balance);
                    i.putExtras(mBundle);
                    startActivity(i);
                }
            }
        });

        btnMoisture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdvancedActivity.this, MoistureActivity.class);
                startActivity(i);
            }
        });




    }

    private void fetchCoordinates(){
        mRootRef.child("location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String,Float> dataMap = (Map)snapshot.getValue();
                Number latitudeN = (Number) dataMap.get("lat");
                Number longitudeN = (Number) dataMap.get("longitude");
                Number balanceN = (Number) dataMap.get("balance");
                AdvancedActivity.latitude = latitudeN.floatValue();
                AdvancedActivity.longitude = longitudeN.floatValue();
                AdvancedActivity.balance = balanceN.intValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdvancedActivity.this,"Failed to load coordinates from database",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }


}