package com.example.hares.movies.Retrofit;

import android.graphics.Bitmap;

public class CategoryMovieResult {
    private final static String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/";
    private Bitmap image ;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    private String name;
    private String id;
    private String title;
    private String poster_path;
    private String vote_average;
    // for videos
    private String key;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    // for reviews
    private String author;
    private String content;

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster_path() {
        return IMAGE_BASE_URL + poster_path;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getName() {
        return name;
    }
}
