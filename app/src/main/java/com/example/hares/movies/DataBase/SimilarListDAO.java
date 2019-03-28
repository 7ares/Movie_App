package com.example.hares.movies.DataBase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SimilarListDAO {
    @Insert
    void addSimilarMovie(SimilarMoviesDetail similarMoviesDetail);

    @Query("SELECT * FROM similarMovies WHERE id = :id")
    LiveData<List<SimilarMoviesDetail>> readListOfMovie(int id);
}
