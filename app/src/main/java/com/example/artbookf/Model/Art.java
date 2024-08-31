package com.example.artbookf.Model;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Art {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @Nullable
    @ColumnInfo(name="name")
    public String name;

    @Nullable
    @ColumnInfo(name="descip")
    public String descip;

    @Nullable
    @ColumnInfo(name="image")
    public byte[] image;


    public Art(String name,@Nullable String descip,@Nullable byte[] image ){
        this.name=name;
        this.descip=descip;
        this.image=image;
    }

}
