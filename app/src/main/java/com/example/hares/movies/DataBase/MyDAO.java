package com.example.hares.movies.DataBase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface MyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addMovie(FavoriteMovieDetail newFavoriteMovie);

    @Delete
    int deleteMovie(FavoriteMovieDetail movie);

    @Query("SELECT * FROM FavoriteList" )
    LiveData <List<FavoriteMovieDetail>> readListOfMovie();

    @Query("SELECT * FROM FavoriteList WHERE MOVIE_ID = :mId ")
    LiveData <FavoriteMovieDetail> getMovie(int mId);
}
