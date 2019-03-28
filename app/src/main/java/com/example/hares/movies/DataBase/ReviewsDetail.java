package com.example.hares.movies.DataBase;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "Review", indices = @Index("id"),
        foreignKeys = @ForeignKey(entity = FavoriteMovieDetail.class,
        parentColumns = "MOVIE_ID",
        childColumns = "id",
        onDelete = ForeignKey.CASCADE)
       )
public class ReviewsDetail {
    @PrimaryKey(autoGenerate = true)
    public int reviewId ;
    public int id;

    public String REVIEWS_AUTHOR;
    public String REVIEWS_CONTENT;

   public ReviewsDetail(int id ,String author, String content) {
       this.id = id ;
        this.REVIEWS_AUTHOR = author;
        this.REVIEWS_CONTENT = content;
    }

    ReviewsDetail() {
    }
}
