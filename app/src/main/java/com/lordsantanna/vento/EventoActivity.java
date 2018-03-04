package com.lordsantanna.vento;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference eventsRef = database.getReference("event");
        user = FirebaseAuth.getInstance().getCurrentUser();

        final TextView name = findViewById(R.id.tv_title);
        final TextView dates = findViewById(R.id.tv_date);
        final TextView time = findViewById(R.id.tv_time);
        final TextView description = findViewById(R.id.tv_description);
        final Button button = findViewById(R.id.bt_action);
        final TextView participantes = findViewById(R.id.numberofparticipants);

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
                participantes.setText(String.valueOf((int) dataSnapshot.child(key).child("joined").getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(EventoActivity.this).create();
                //alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to join this event?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String prova = FirebaseDatabase.getInstance().getReference("event").child(key).child("joined").child(user.getDisplayName().toString()).getKey();
                               // if(prova == null) {
                                    FirebaseDatabase.getInstance().getReference("event").child(key).child("joined").child(user.getDisplayName().toString()).setValue("1");

                                    Toast.makeText(EventoActivity.this, "Congratulations! You've joined this event.", Toast.LENGTH_SHORT).show();
                                    finish();
                               /* }
                                else{
                                    Toast.makeText(EventoActivity.this, "You've already joined this event.", Toast.LENGTH_SHORT).show();

                                }*/
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


    }
}
//TODO AUTH NOT REALLY WORKING