package com.lordsantanna.vento;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.lordsantanna.vento.utils.DatePickerFragment;
import com.lordsantanna.vento.utils.TimePickerFragment;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Calendar;
import java.util.Date;

import static android.R.attr.data;

/**
 * Created by LordSantAnna on 03/03/2018.
 */

public class CrearEvento extends AppCompatActivity {
    EditText titol, tv_date, time, description;
    ImageView iv_map;
    Button addevent;
    Calendar date;
    LatLng position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createevent);
        date = Calendar.getInstance();
        titol = (EditText) findViewById(R.id.title);
        tv_date = (EditText) findViewById(R.id.date);
        time = (EditText) findViewById(R.id.time);
        description = (EditText) findViewById(R.id.description);
        addevent = (Button) findViewById(R.id.addevent);
        iv_map = (ImageView) findViewById(R.id.iv_map);
        position = (LatLng) getIntent().getParcelableExtra("position");
        //TODO: position null
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        if(position == null){
            position.setLatitude(41.40099);
            position.setLongitude( 2.19876);
        }
//Todo usuaei que vn a un event
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        addevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(titol.getText().length()>0) {
                    event evento = new event(titol.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString(), description.getText().toString(), date.getTimeInMillis() / 1000, position.getLatitude(), position.getLongitude());
                    //TODO: firebase ususari agafarlo
                    String clau =   FirebaseDatabase.getInstance().getReference("event").push().getKey();
                    FirebaseDatabase.getInstance().getReference("event").child(clau).setValue(evento);
                    //TODO: show toast si falla al penjarse


                    finish();
                }else{
                    Toast.makeText(CrearEvento.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Glide.with(this).load(staticMapURL(position)).centerCrop().into(iv_map);

    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                date.set(year, month, day);

                final String selectedDate = day + " / " + (month+1) + " / " + year;
                tv_date.setText(selectedDate);
            }
        });
        newFragment.show(CrearEvento.this.getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                date.set(Calendar.HOUR_OF_DAY, h);
                date.set(Calendar.MINUTE, m);
                final String selectedTime = h+":"+m;
                time.setText(selectedTime);
            }

        });
        newFragment.show(CrearEvento.this.getSupportFragmentManager(), "timePicker");
    }

    public String staticMapURL(LatLng latLng){
        return "https://api.mapbox.com/styles/v1/mapbox/dark-v9/static/"+latLng.getLongitude()+","+latLng.getLatitude()+",10.0,0,0/400x300@2x?access_token=pk.eyJ1IjoiY2FybG9zYWlyIiwiYSI6ImNpeGV1dnBwcDAwMnEyenNid3FtcjVnb24ifQ.muuRu5nSACwjkaCMU2Id6g";
    }


}

