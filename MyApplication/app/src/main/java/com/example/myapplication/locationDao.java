package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface locationDao {
    @Query("SELECT * FROM locationEntity")
    public LiveData<List<locationEntity>> getAll();

    @Query("SELECT * FROM locationEntity WHERE today LIKE :input_day")
    List<locationEntity> getData(String input_day);
    //LiveData<List<locationEntity>> getData(String input_day);

    @Insert
    public void insertData(locationEntity data);

    @Update
    public void UpdateData(locationEntity data);

    @Query("INSERT INTO locationEntity(ID, today, latitude, longitude) VALUES (:id,:input_day,:la, :lo)")
    public void insertData(int id, Double la, Double lo, String input_day);
}
