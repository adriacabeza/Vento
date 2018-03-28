package com.lordsantanna.vento;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.android.telemetry.location.LostLocationEngine;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;



/**
 * public class MainActivity extends AppCompatActivity {
 * private FirebaseAuth mAuth;
 *
 * @Override protected void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState);
 * setContentView(R.layout.activity_main);
 * <p>
 * <p>
 * FloatingActionButton mbutton = (FloatingActionButton) findViewById(R.id.eventadd);
 * Button mbutton2 = (Button) findViewById(R.id.button2);
 * <p>
 * <p>
 * <p>
 * else{ mbutton.setOnClickListener(new View.OnClickListener() {
 * @Override public void onClick(View view) {
 * Intent intent1 = new Intent(MainActivity.this,CrearEvento.class);
 * startActivity(intent1);
 * }
 * });
 * <p>
 * mbutton2.setOnClickListener(new View.OnClickListener() {
 * @Override public void onClick(View view) {
 * Intent intent2 = new Intent(MainActivity.this,userwin.class);
 * startActivity(intent2);
 * }
 * });
 * }
 * <p>
 * //TODO :DIALOG CANVIAR MERDA
 * <p>
 * }
 * <p>
 * }
 **/

/**
 * Use the Location Layer plugin to easily add a device location "puck" to a Mapbox map.
 */
public class MainActivity extends AppCompatActivity implements LocationEngineListener, PermissionsListener {
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    LatLng myLocation;
    private FirebaseUser user;
    BiMap<Long, String> markers;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getResources().getString(R.string.mapbox_key));
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference eventsRef = database.getReference("event");

        markers = HashBiMap.create();

        if (user == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            final user usuari = new user(user.getDisplayName().toString(), user.getPhoneNumber() , user.getEmail().toString());
            FirebaseDatabase.getInstance().getReference("usuari").child(usuari.name).setValue(usuari);
        }

        FloatingActionButton mbutton = (FloatingActionButton) findViewById(R.id.eventadd);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newEvent(mapboxMap.getCameraPosition().target, true);
            }
        });

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                MainActivity.this.mapboxMap = mapboxMap;
                Integer ab_height = getSupportActionBar().getHeight();
                mapboxMap.getUiSettings().setCompassMargins(mapboxMap.getUiSettings().getCompassMarginLeft(),mapboxMap.getUiSettings().getCompassMarginTop() + ab_height,mapboxMap.getUiSettings().getCompassMarginRight(), mapboxMap.getUiSettings().getCompassMarginBottom());

                enableLocationPlugin();

                mapboxMap.setOnInfoWindowClickListener(new MapboxMap.OnInfoWindowClickListener() {
                    @Override
                    public boolean onInfoWindowClick(@NonNull Marker marker) {
                        Intent intent = new Intent(MainActivity.this, EventoActivity.class);
                        String FBkey = markers.get(marker.getId());
                        intent.putExtra("key", FBkey);
                        startActivity(intent);
                        // return false to close the info window
                        // return true to leave the info window open
                        return false;
                    }
                });

                final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull final LatLng point) {
                        vibe.vibrate(50);
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        //alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Create new event?");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        newEvent(point, false);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                });

                eventsRef.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String FBkey = dataSnapshot.getKey();
                        Marker marker = mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng((double) dataSnapshot.child("lat").getValue(), (double) dataSnapshot.child("lng").getValue()))
                                .title(dataSnapshot.child("titol").getValue().toString())
                                .snippet((String) dataSnapshot.child("usuari").getValue()));
                        markers.put(marker.getId(), FBkey);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Marker m = (Marker) mapboxMap.getAnnotation(markers.inverse().get(dataSnapshot.getKey()));
                        m.setPosition(new LatLng((double) dataSnapshot.child("lat").getValue(),  (double) dataSnapshot.child("lng").getValue()));
                        m.setTitle(dataSnapshot.child("titol").getValue().toString());
                        m.setSnippet((String) dataSnapshot.child("usuari").getValue());
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Marker m = (Marker) mapboxMap.getAnnotation(markers.inverse().get(dataSnapshot.getKey()));
                        m.remove();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

        });
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                if(tabId == R.id.bbn_item1){}
                else if(tabId == R.id.bbn_item2){}
                else{
                    Intent intent2 = new Intent(MainActivity.this,userwin.class);
                    startActivity(intent2);
                }
            }
        });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, mapboxMap, locationEngine);
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = new LostLocationEngine(MainActivity.this);
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 11));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
            myLocation = new LatLng(location);
        }
    }

    public void newEvent(LatLng latLng, boolean pickLocationFirst){
        Intent intent = new Intent(MainActivity.this, CrearEvento.class);
        intent.putExtra("location", latLng);
        if (pickLocationFirst) intent.putExtra("pickLocationFirstZoom", mapboxMap.getCameraPosition().zoom);
        startActivity(intent);
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}