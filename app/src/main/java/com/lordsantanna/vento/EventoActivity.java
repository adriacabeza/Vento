package com.lordsantanna.vento;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference eventsRef = database.getReference("event");

        final TextView name = findViewById(R.id.tv_title);
        final TextView dates = findViewById(R.id.tv_date);
        final TextView time = findViewById(R.id.tv_time);
        final TextView description = findViewById(R.id.tv_description);

        final String key = getIntent().getStringExtra("key");
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child(key).child("titol").getValue().toString());
                description.setText(dataSnapshot.child(key).child("info").getValue().toString());
                Date date = new Date((Long) dataSnapshot.child(key).child("data").getValue()*1000);
                SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
                String strDt = simpleDate.format(date);
                dates.setText(strDt);
                SimpleDateFormat simpletime =  new SimpleDateFormat("hh:mm");
                String strTm = simpletime.format(date);
                time.setText(strTm);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
//TODO AUTH NOT REALLY WORKING