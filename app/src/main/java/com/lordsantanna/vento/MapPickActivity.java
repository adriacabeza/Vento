package com.lordsantanna.vento;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.commons.geojson.Point;

public class MapPickActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap mapboxMap;
    LatLng location;
    ImageView hoveringMarker;
    Button bt_pick;
    Double initialZoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pick);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Mapbox.getInstance(this, getString(R.string.mapbox_key));
        setContentView(R.layout.activity_map_pick);
        mapView = (MapView) findViewById(R.id.mapView);
        bt_pick = (Button) findViewById(R.id.bt_pick);
        hoveringMarker = (ImageView) findViewById(R.id.iv_marker);
        mapView.onCreate(savedInstanceState);

        location = (LatLng) getIntent().getParcelableExtra("location");
        initialZoom = getIntent().getDoubleExtra("zoom", 16.0);

        String title = getIntent().getStringExtra("title");
        if(title.isEmpty()) setTitle("Vento Event");
        else setTitle(title);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                MapPickActivity.this.mapboxMap = mapboxMap;

                ViewGroup.MarginLayoutParams vlp = (ViewGroup.MarginLayoutParams) bt_pick.getLayoutParams();
                Integer bt_pick_Height = bt_pick.getMeasuredHeight()+vlp.bottomMargin;
                Integer bt_pick_LeftMargin = vlp.leftMargin;
                Integer ab_height = getSupportActionBar().getHeight();

                mapboxMap.getUiSettings().setAttributionMargins(mapboxMap.getUiSettings().getAttributionMarginLeft()+bt_pick_LeftMargin,mapboxMap.getUiSettings().getAttributionMarginTop(),mapboxMap.getUiSettings().getAttributionMarginRight(), mapboxMap.getUiSettings().getAttributionMarginBottom()+bt_pick_Height);
                mapboxMap.getUiSettings().setLogoMargins(mapboxMap.getUiSettings().getLogoMarginLeft()+bt_pick_LeftMargin,mapboxMap.getUiSettings().getLogoMarginTop(),mapboxMap.getUiSettings().getLogoMarginRight(), mapboxMap.getUiSettings().getLogoMarginBottom()+bt_pick_Height);
                mapboxMap.getUiSettings().setCompassMargins(mapboxMap.getUiSettings().getCompassMarginLeft(),mapboxMap.getUiSettings().getCompassMarginTop() + ab_height,mapboxMap.getUiSettings().getCompassMarginRight(), mapboxMap.getUiSettings().getCompassMarginBottom()+bt_pick_Height);
                mapboxMap.setPadding(0,0,0, 0);
                mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, initialZoom));
            }
         });

        bt_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapboxMap != null) {
                    float coordinateX = hoveringMarker.getLeft() + (hoveringMarker.getWidth() / 2);
                    float coordinateY = hoveringMarker.getTop()+ (hoveringMarker.getHeight() / 2);

                    float[] coords = new float[] {coordinateX, coordinateY};
                    location = new LatLng(  mapboxMap.getProjection().fromScreenLocation(new PointF(coords[0], coords[1])).getLatitude(),
                                            mapboxMap.getProjection().fromScreenLocation(new PointF(coords[0], coords[1])).getLongitude());

                    Intent data = new Intent();
                    data.putExtra("location", location);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });


    }

    // Add the mapView's own lifecycle methods to the activity's lifecycle methods
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
