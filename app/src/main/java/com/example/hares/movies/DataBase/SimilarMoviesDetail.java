package com.example.hares.movies.DataBase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "similarMovies",indices = @Index("id"),
        foreignKeys = @ForeignKey(entity = FavoriteMovieDetail.class,
                parentColumns = "MOVIE_ID",
                childColumns = "id",
                onDelete = CASCADE)
)
public class SimilarMoviesDetail {
    @PrimaryKey(autoGenerate = true)
    public int MO_ID;

    public int id;

    public String SIMILAR_MOVIE_NAME;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] SIMILAR_MOVIE_PICS;

    public SimilarMoviesDetail(int id, String name,byte[] pic) {
        this.id = id;
        this.SIMILAR_MOVIE_NAME = name;
        this.SIMILAR_MOVIE_PICS = pic;
    }

    SimilarMoviesDetail() {
    }

}
