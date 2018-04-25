package com.example.maxha.myapplication;

import android.arch.persistence.room.*;


import java.util.List;

@Dao
public interface TrainDao{
    @Query("SELECT * FROM Train")
    List<Train> getAll();

    @Query("SELECT * FROM Train WHERE id IN (:Ids)")
    List<Train> loadAllByIds(int[] Ids);

    @Query("SELECT * FROM Train WHERE train IS (:name)")
    List<Train> name(String name);

    @Insert
    void insertAll(Train... table);

    @Delete
    void delete(Train table);
}