package com.example.artbookf.Roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.artbookf.Model.Art;

@Database(entities = {Art.class},version = 1)
public abstract class ArtDatabase extends RoomDatabase {
    public abstract ArtDao artDao();
}
