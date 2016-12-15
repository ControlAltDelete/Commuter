package com.machineproject.commuter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import controller.SystemEventController;
import model.NavItem;
import model.Terminal;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
    private LocationRequest mLocationRequest;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private final static int LOCATION = 100;
    private final static String TAG = MapsActivity.class.getSimpleName();

    private ArrayList<NavItem> mNavItems;
    private RelativeLayout mDrawerPane;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private  DrawerListAdapter adapter;

    private SystemEventController systemEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("Commuter");

        populateDrawer();

        systemEvent = SystemEventController.getInstance();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(user.getEmail());
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        systemEvent.loadAllMarkers(mMap, getBaseContext(), MapsActivity.this);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            if (mAuth.getCurrentUser().getEmail().equals("aljonjose@gmail.com")) {
                systemEvent.onMainMapClick(mMap, getBaseContext(), MapsActivity.this);
            }
        }
        else {
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (lastLocation != null) {
                handleNewLocation(lastLocation);
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        //handleNewLocation(location);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private void handleNewLocation(Location location)
    {
        LatLng view = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(view, 15.0f));
    }


    private void populateDrawer()
    {
        mNavItems = new ArrayList<NavItem>();
        mNavItems.add(new NavItem("Home", "", R.drawable.ic_home_black_24dp));
        mNavItems.add(new NavItem("Log-out", "", R.drawable.ic_lock_open_black_24dp));
        mNavItems.add(new NavItem("Refresh Map", "", R.drawable.ic_repeat_black_24dp));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        adapter = new DrawerListAdapter(getBaseContext(), mNavItems);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerList.setItemChecked(position, true);


                if (position == 1)
                {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();

                    Intent mainAct = new Intent();
                    mainAct.setClass(getBaseContext(), MainActivity.class);
                    startActivity(mainAct);
                    finish();
                }

                else if (position == 2) {
                    systemEvent.refreshMarkers(mMap, getBaseContext(), MapsActivity.this);
                    Toast.makeText(getBaseContext(), "Map refreshed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
