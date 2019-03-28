package com.example.hares.movies.DataBase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;



@Entity(tableName = "FavoriteList", indices = @Index("MOVIE_ID"))
public class FavoriteMovieDetail {
    @PrimaryKey
    @ColumnInfo(name = "MOVIE_ID")
    public int M_ID;
    public String MOVIE_NAME;
    public String MOVIE_VOTE_AVERAGE;
    public String MOVIE_RELEASE_YEAR;
    public String MOVIE_US_CERTIFICATION;
    public String MOVIE_OVERVIEW;
    public String MOVIE_VOTE_COUNT;
    public String MOVIE_GENRES;


    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] MOVIE_BACKDROP;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] MOVIE_POSTER;

    public FavoriteMovieDetail(String movieId, String movieName, String movieVoteAverage, String voteCount, byte[] moviePic,
                               byte[] backdrop, String year, String certification, String overView, String genres) {
        this.M_ID = Integer.valueOf(movieId);
        this.MOVIE_NAME = movieName;
        this.MOVIE_VOTE_AVERAGE = movieVoteAverage;
        this.MOVIE_POSTER = moviePic;
        this.MOVIE_VOTE_COUNT = voteCount;
        this.MOVIE_BACKDROP = backdrop;
        this.MOVIE_RELEASE_YEAR = year;
        this.MOVIE_US_CERTIFICATION = certification;
        this.MOVIE_OVERVIEW = overView;
        this.MOVIE_GENRES = genres;


    }

    public FavoriteMovieDetail() {
    }


}
