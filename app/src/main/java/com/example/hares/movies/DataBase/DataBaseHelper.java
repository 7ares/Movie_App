package com.example.hares.movies.DataBase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = {FavoriteMovieDetail.class, SimilarMoviesDetail.class, ReviewsDetail.class}, version = 1, exportSchema = false)

public abstract class DataBaseHelper extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "try1";
    private static DataBaseHelper sInstance;

    public static DataBaseHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        DataBaseHelper.class, DataBaseHelper.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract MyDAO myDAO();

    public abstract ReviewsDAO reviewsDAO();

    public abstract SimilarListDAO similarMovieDAO();
}


