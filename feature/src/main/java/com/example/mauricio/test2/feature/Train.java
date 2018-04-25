package com.example.maxha.myapplication;

import android.arch.persistence.room.*;

@Entity
public class Train{
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "train")
    private String train;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrain() {
        return train;
    }

    public void setTrain(String train) {
        this.train = train;
    }
}