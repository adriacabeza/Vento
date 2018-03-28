package com.lordsantanna.vento;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lordsantanna.vento.utils.DatePickerFragment;
import com.lordsantanna.vento.utils.MapUtils;
import com.lordsantanna.vento.utils.TimePickerFragment;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.R.attr.data;

/**
 * Created by LordSantAnna on 03/03/2018.
 */

public class CrearEvento extends AppCompatActivity {
    private FirebaseUser FBuser;
    EditText et_title, et_date, et_time, et_description;
    ImageView iv_map;
    Button bt_action_event;
    Calendar date;
    LatLng location;
    String key;
    boolean isCreating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createevent);
        date = Calendar.getInstance();
        et_title = (EditText) findViewById(R.id.et_title);
        et_date = (EditText) findViewById(R.id.et_date);
        et_time = (EditText) findViewById(R.id.et_time);
        et_description = (EditText) findViewById(R.id.et_description);
        bt_action_event = (Button) findViewById(R.id.bt_action_event);
        iv_map = (ImageView) findViewById(R.id.iv_map);

        isCreating = !(getIntent().hasExtra("key"));
        if(isCreating){
            location = (LatLng) getIntent().getParcelableExtra("location");
            if(location == null){ //TODO: position null
                location.setLatitude(41.40099);
                location.setLongitude( 2.19876);
            }
            Glide.with(this).load(MapUtils.staticMapURL(location, 14, this)).centerCrop().into(iv_map);
        }else{
            bt_action_event.setText("Save");

            key = getIntent().getStringExtra("key");

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference eventsRef = database.getReference("event");
            FBuser = FirebaseAuth.getInstance().getCurrentUser();

            eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    et_title.setText(dataSnapshot.child(key).child("titol").getValue().toString());
                    et_description.setText(dataSnapshot.child(key).child("info").getValue().toString());
                    date.setTimeInMillis((Long) dataSnapshot.child(key).child("data").getValue()*1000);
                    SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
                    String strDt = simpleDate.format(date.getTime());
                    et_date.setText(strDt);
                    SimpleDateFormat simpletime =  new SimpleDateFormat("HH:mm");
                    String strTm = simpletime.format(date.getTime());
                    et_time.setText(strTm);
                    location = new LatLng((double) dataSnapshot.child(key).child("lat").getValue(), (double) dataSnapshot.child(key).child("lng").getValue());
                    Glide.with(CrearEvento.this).load(MapUtils.staticMapURL(location, 14, CrearEvento.this)).centerCrop().into(iv_map);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        iv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrearEvento.this, MapPickActivity.class);
                intent.putExtra("location", location);
                intent.putExtra("title", et_title.getText().toString());
                startActivityForResult(intent,1);

            }
        });

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        //Todo usuaei que vn a un event
        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        bt_action_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_title.getText().length()>0) {
                    event evento = new event(et_title.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), et_description.getText().toString(), date.getTimeInMillis() / 1000, location.getLatitude(), location.getLongitude());
                    if(isCreating){
                        String clau = FirebaseDatabase.getInstance().getReference("event").push().getKey();
                        FirebaseDatabase.getInstance().getReference("event").child(clau).setValue(evento);
                    }else{
                        FirebaseDatabase.getInstance().getReference("event").child(key).child("data").setValue(evento.data);
                        FirebaseDatabase.getInstance().getReference("event").child(key).child("info").setValue(evento.info);
                        FirebaseDatabase.getInstance().getReference("event").child(key).child("lat").setValue(evento.lat);
                        FirebaseDatabase.getInstance().getReference("event").child(key).child("lng").setValue(evento.lng);
                        FirebaseDatabase.getInstance().getReference("event").child(key).child("titol").setValue(evento.titol);
                    }
                    //TODO: firebase ususari agafarlo
                    //TODO: show toast si falla al penjarse
                    finish();
                }else{
                    Toast.makeText(CrearEvento.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, month);
                date.set(Calendar.DAY_OF_MONTH, day);
                SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
                String strDt = simpleDate.format(date.getTime());
                et_date.setText(strDt);

                if(TextUtils.isEmpty(et_time.getText().toString())) showTimePickerDialog();

            }
        }, date);
        newFragment.show(CrearEvento.this.getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                date.set(Calendar.HOUR_OF_DAY, h);
                date.set(Calendar.MINUTE, m);
                SimpleDateFormat simpletime =  new SimpleDateFormat("HH:mm");
                String strTm = simpletime.format(date.getTime());
                et_time.setText(strTm);
            }

        }, date);
        newFragment.show(CrearEvento.this.getSupportFragmentManager(), "timePicker");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                location = (LatLng) data.getParcelableExtra("location");
                Glide.with(this).load(MapUtils.staticMapURL(location, 14, this)).centerCrop().into(iv_map);
            }
        }
    }
}