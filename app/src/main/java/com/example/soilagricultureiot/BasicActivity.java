package com.example.soilagricultureiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class BasicActivity extends AppCompatActivity {
    Button btnSignOut;
    Button btnAdvancedMode;
    TextView tvWelcome;
    TextView userName;
    TextView moisture_text;
    TextView humidity_text;
    TextView ph_text;
    TextView smoke_text;
    TextView moisture_value;
    TextView ph_value;
    TextView humidity_value;
    TextView smoke_value;
    ImageView moisture_pic, ph_pic,humidity_pic,smoke_pic;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private FirebaseDatabase mRootRef;
    private static final String TAG = "homeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        TextView moisture_text = (TextView) findViewById(R.id.moisture_text);
        TextView humidity_text = (TextView) findViewById(R.id.humidity_text);
        TextView ph_text = (TextView) findViewById(R.id.ph_text);
        TextView smoke_text = (TextView) findViewById(R.id.smoke_text);
        final TextView moisture_value = (TextView) findViewById(R.id.moisture_value);
        final TextView ph_value = (TextView) findViewById(R.id.ph_value);
        final TextView humidity_value = (TextView) findViewById(R.id.humidity_value);
        final TextView smoke_value = (TextView) findViewById(R.id.smoke_value);

        ImageView moisture_pic = (ImageView) findViewById(R.id.moisture_pic);
        ImageView ph_pic = (ImageView) findViewById(R.id.ph_pic);
        ImageView humidity_pic = (ImageView) findViewById(R.id.humidity_pic);
        ImageView smoke_pic = (ImageView) findViewById(R.id.smoke_pic);
        btnSignOut = (Button) findViewById(R.id.btnSignOut);
        btnAdvancedMode = (Button) findViewById(R.id.btnAdvancedMode);
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        userName = (TextView) findViewById(R.id.userName);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mRoot = FirebaseDatabase.getInstance();

        String user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userName.setText(user_email);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BasicActivity.this, MainActivity.class);
                Toast.makeText(BasicActivity.this, "Signing out...",
                        Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                startActivity(i);
            }
        });

        btnAdvancedMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BasicActivity.this, AdvancedActivity.class);
                Toast.makeText(BasicActivity.this, "Going Research Mode", Toast.LENGTH_SHORT).show();
                startActivity(i);
            }
        });


        DatabaseReference mRootRef = mRoot.getReference();
        DatabaseReference gasRef = mRootRef.child("firebaseIOT").child("gas");


        //Firebase data fetch function

        mRootRef.child("firebaseIOT").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Float> dataMap = (Map)snapshot.getValue();

                //mapping incoming object to key pair values with map function
                //sourcing individual values with .get method.
                Number gasN = (Number) dataMap.get("gas");
                Number phN = (Number) dataMap.get("ph");
                Number moistureN= (Number) dataMap.get("moisture");
                Number humidityN= (Number) dataMap.get("humidity");

                Float gas = gasN.floatValue();
                Float ph = phN.floatValue();
                Float moisture= moistureN.floatValue();
                Float humidity= humidityN.floatValue();

//                Float gas = dataMap.get("gas");
//                Float ph = dataMap.get("ph");
//                Float moisture= dataMap.get("moisture");
//                Float humidity= dataMap.get("humidity");

                humidity_value.setText(Float.toString(humidity));
                moisture_value.setText(Float.toString(moisture));
                smoke_value.setText(Float.toString(gas));
                ph_value.setText(Float.toString(ph));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(BasicActivity.this,"Failed to load data from database",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}