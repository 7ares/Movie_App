package com.example.hares.movies.DataBase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ReviewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addReview (ReviewsDetail reviewsDetail);

    @Query("SELECT * FROM Review WHERE id = :id")
    LiveData<List<ReviewsDetail>> readListOfReviews(int id);
}
