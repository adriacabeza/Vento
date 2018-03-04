package com.lordsantanna.vento;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by LordSantAnna on 02/03/2018.
 */

public class userwin extends AppCompatActivity  {
         private FirebaseAuth mAuth;
        private FirebaseUser user;
        private String m_Text = "";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.userwin);

            mAuth = FirebaseAuth.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
        //EMPLENAR FOTO
            ImageView image = (ImageView) findViewById(R.id.foto);
            Glide.with(this).load(user.getPhotoUrl().toString()).into(image);

            //CREO EL USUARI
            //TODO name before shrek
            final user usuari = new user(user.getDisplayName().toString(), user.getPhoneNumber() , user.getEmail().toString());

            FirebaseDatabase.getInstance().getReference("usuari").child(usuari.name).setValue(usuari);
            //TEXT VIEW ON HI HA EL NOM
            final TextView nom = (TextView) findViewById(R.id.nom);
            nom.setText(usuari.name);

            //EMPLENAR BIO
            final TextView bio = (TextView) findViewById(R.id.bio);
            bio.setText(usuari.bio);

            //CANVIAR BIO

            bio.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String desc = changetext("bio");
                    bio.setText(desc);
                    usuari.bio = desc;
                    return false;
                }
            });


            //CANVIAR NOM
            nom.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String yee = changetext("name");
                    nom.setText(yee);
                    usuari.name = yee;
                    return false;
                }
            });
        }


        public String changetext(String what){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Change your " + what);

// Set up the input
            final EditText input = new EditText(this);

            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return "MA BOIII";
        }
}



//TODO: veure totes les fotos del evento



