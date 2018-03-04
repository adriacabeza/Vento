package com.lordsantanna.vento;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by LordSantAnna on 03/03/2018.
 */

public class event {
    //public String image;
    public String titol;
    public String usuari;
    public String info;
    public long data;
    public double lat;
    public double lng;
    //TODO: amics


    public event(String titol,String usuari,String info, long data, double lat, double lng){
        this.titol = titol;
        this.usuari = usuari;
        this.info = info;
        this.data = data;
        this.lat = lat;
        this.lng = lng;
    }

    public event(){};
}
