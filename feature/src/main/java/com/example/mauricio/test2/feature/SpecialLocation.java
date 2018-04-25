package com.example.maxha.myapplication;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SpecialLocation {
        @PrimaryKey
        private int id;

        @ColumnInfo(name = "name")
        private String name;

        @ColumnInfo(name = "Lng")
        private double lng;

        @ColumnInfo(name = "Lat")
        private double lat;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
