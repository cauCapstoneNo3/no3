package com.example.myapplication;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

//@Entity(tableName = "locationEntity", indices = {@index("date")})
//@Entity(tableName ="locationEntity", indices = {@Index("today"), @Index(value ={"today", "ID"})})
@Entity(tableName ="locationEntity", indices = {@Index(value ={"today", "ID"})})
public class locationEntity {
    //Calendar cal = Calendar.getInstance();
    //@PrimaryKey(autoGenerate = true)
    @NonNull
    @PrimaryKey(autoGenerate = true)
    int ID;

    private String today;
    //String today = cal.get(Calendar.YEAR)+Integer.toString(cal.get(Calendar.MONTH)+1)+cal.get(Calendar.DATE);;

    private Double latitude;
    //private Location[] location;

    private Double longitude;

    private int markerFlag = 0;

    private String textData = null;

    private int alreadyCHK = 0;

    // images 되면 이거 어떻게 하지? datapath 저장하는 방식으로 바꿔?
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image = null;


    public void setData(int ID, Double la, Double lo, String today){
        this.ID = ID;
        this.today = today;
        this.latitude = la;
        this.longitude =lo;
    }

    public void setData(Double la, Double lo, String today){
        this.today = today;
        this.latitude = la;
        this.longitude =lo;
    }

    public  int getID(){ return this.ID; }

    public String getToday(){
        return this.today;
    }

    public Double getLongitude(){
        return longitude;
    }

    public Double getLatitude(){
        return latitude;
    }

    public byte[] getImage(){ return this.image;}

    public String getTextData(){ return this.textData;}

    public int getMarkerFlag(){ return this.markerFlag;}

    public int getAlreadyCHK(){return this.alreadyCHK;}

    public void setToday(String inputtoday){
        this.today = inputtoday;
    }

    public void setLatitude(Double inputlatitude){
        this.latitude = inputlatitude;
    }

    public void setLongitude(Double inputlongitude){
        this.longitude = inputlongitude;
    }

    public void setImage(byte[] inputimage){ this.image = inputimage;}

    public void setTextData(String inputtextData){ this.textData = inputtextData; }

    public void setMarkerFlag(int inputFlag){ this.markerFlag = inputFlag; }

    public void setAlreadyCHK(int inputCHK){ this.alreadyCHK = inputCHK; }
}
