package com.lordsantanna.vento;

import android.widget.ImageView;

/**
 * Created by LordSantAnna on 02/03/2018.
 */

public class user {

    //public String image;
    public String name;
    public String bio;
    public String telf;
    public String mail;
    //TODO: amics


    public user(String name, String telf, String mail){
        this.telf = telf;
        this.name = name;
        this.mail = mail;
        this.bio = "Change your bio Long pressing the text";
    }

    public user(){};


    public void setBio(String bio){
        this.bio = bio;
    }
    public void setName(String name){
        this.name = name;
    }


}
