package com.example.huy.chiaseanh;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Serializable {

    private GoogleMap mMap;
    LatLong latLong = new LatLong();
    private Geocoder geocoder;
    private String[] locationArr = new String[10];
    private String strLocation;
    private ImageView btnBackMap;
    private String uID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnBackMap = (ImageView) findViewById(R.id.btnBackMap);
        latLong = (LatLong) getIntent().getSerializableExtra("location");
        uID = getIntent().getExtras().getString("Uid");
        btnBackMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, MainActivity.class);
                i.putExtra("Uid", uID);
                MapsActivity.this.startActivity(i);

            }
        });

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
        if (latLong != null)
        {
            List<Address> addresses = null;
            try
            {
                geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                addresses = geocoder.getFromLocation(latLong.getLat(), latLong.getLng(), 1);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            int maxAddressLine = addresses.get(0).getMaxAddressLineIndex();
            String cityName = addresses.get(0).getAddressLine(maxAddressLine);
            locationArr = cityName.split(", ");

            strLocation = locationArr[locationArr.length - 5] + "," + locationArr[locationArr.length - 4] + "," + locationArr[locationArr.length - 3] + ", " + locationArr[locationArr.length - 2] + ", " + locationArr[locationArr.length - 1];

        }
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(latLong.getLat(), latLong.getLng());
        mMap.addMarker(new MarkerOptions().position(location).title(strLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15F));
    }
}
