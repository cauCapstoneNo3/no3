package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName ="dailyEntity")
public class dailyEntity {
    //Calendar cal = Calendar.getInstance();
    //@PrimaryKey(autoGenerate = true)
    @NonNull
    @PrimaryKey(autoGenerate = true)
    int ID;

    private String today;

    private String textData;

    public void setData(int ID, String today, String textData){
        this.ID = ID;
        this.today = today;
        this.textData = textData;
    }

    public void setData(String today, String textData){
        this.today = today;
        this.textData = textData;
    }

    public String getTextData(){ return this.textData;}

    public String getToday(){
        return this.today;
    }

    public void setTextData(String textData){ this.textData = textData; }

    public void setToday(String today){
        this.today = today;
    }

}
