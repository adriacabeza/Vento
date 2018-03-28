package com.lordsantanna.vento;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lordsantanna.vento.utils.MapUtils;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EventoActivity extends AppCompatActivity {
    TextView tv_name, tv_creator, tv_date, tv_time, tv_description, tv_participants;
    ImageView iv_map;
    Button bt_action;
    LatLng location;
    Calendar date;
    String name, creator, user;
    int nparticipants;
    String key;
    boolean user_is_creator = false;
    DatabaseReference eventsRef;
    private FirebaseUser FBuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        eventsRef = database.getReference("event");
        FBuser = FirebaseAuth.getInstance().getCurrentUser();

        tv_name = findViewById(R.id.tv_title);
        tv_creator = findViewById(R.id.tv_creator);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        tv_description = findViewById(R.id.tv_description);
        bt_action = findViewById(R.id.bt_action);
        tv_participants = findViewById(R.id.numberofparticipants);
        iv_map = findViewById(R.id.iv_map);

        key = getIntent().getStringExtra("key");

        bt_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(EventoActivity.this).create();
                //alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to join this event?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String prova = FirebaseDatabase.getInstance().getReference("event").child(key).child("joined").child(user).getKey();
                               // if(prova == null) {
                                    FirebaseDatabase.getInstance().getReference("event").child(key).child("joined").child(user).setValue("1");

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

        iv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(location!=null){
                    Uri gmmIntentUri = Uri.parse("geo:"+location.getLatitude()+","+location.getLongitude()+"?q="+location.getLatitude()+","+location.getLongitude()+"("+Uri.encode(name)+")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event, menu);

        MenuItem item = menu.findItem(R.id.action_edit);
        item.setVisible(user_is_creator);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(EventoActivity.this, CrearEvento.class);
                intent.putExtra("key", key);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child(key).child("titol").getValue().toString();
                tv_name.setText(name);
                creator = dataSnapshot.child(key).child("usuari").getValue().toString();
                tv_creator.setText(creator);
                tv_description.setText(dataSnapshot.child(key).child("info").getValue().toString());
                date = Calendar.getInstance();
                date.setTimeInMillis((Long) dataSnapshot.child(key).child("data").getValue()*1000);
                SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
                String strDt = simpleDate.format(date.getTime());
                tv_date.setText(strDt);
                SimpleDateFormat simpletime =  new SimpleDateFormat("HH:mm");
                String strTm = simpletime.format(date.getTime());
                tv_time.setText(strTm);
                nparticipants = (int) dataSnapshot.child(key).child("joined").getChildrenCount();
                tv_participants.setText(String.valueOf(nparticipants)+" joined");
                location = new LatLng((double) dataSnapshot.child(key).child("lat").getValue(), (double) dataSnapshot.child(key).child("lng").getValue());
                Glide.with(EventoActivity.this).load(MapUtils.staticMapURL(location, 14, EventoActivity.this)).centerCrop().into(iv_map);

                user = FBuser.getDisplayName().toString();

                user_is_creator = user.equals(creator);
                invalidateOptionsMenu();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
//TODO AUTH NOT REALLY WORKING