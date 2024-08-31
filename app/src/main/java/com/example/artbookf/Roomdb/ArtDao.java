package com.example.artbookf.Roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.artbookf.Model.Art;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ArtDao {

    @Query("SELECT id,name,image FROM Art")
    Flowable<List<Art>> getArt();

    @Insert
    Completable insert(Art art);

    @Delete
    Completable delete(Art art);

    @Query("DELETE FROM Art WHERE id = :artId")
    Completable deleteById(int artId);
}
