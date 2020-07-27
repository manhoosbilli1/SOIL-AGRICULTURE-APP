package com.example.soilagricultureiot;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Maps";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle location = getIntent().getExtras();
        assert location != null;
        float latitude = location.getFloat("latitude");
        float longitude = location.getFloat("longitude");
        int balance = location.getInt("balance");
        Log.v(TAG, Float.toString(latitude) +',' + Float.toString(longitude));




        // Add a marker in Sydney and move the camera
        if(latitude != 0.0) {
            LatLng device = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(device).title("Device"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(device));
        } else if(balance == 0){
            Toast.makeText(MapsActivity.this,
                    "Geolocation's daily API quota is already used up," +
                            "Please try again tomorrow." ,Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(MapsActivity.this,"Coorindates didn't generate correctly, Restart Sensor device",
                    Toast.LENGTH_SHORT).show();
        }

    }
}