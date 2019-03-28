package com.example.hares.movies.Retrofit;

import java.util.ArrayList;

import javax.xml.transform.Result;

public class MovieDetail {

    private final static String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/";
    private String backdrop_path;
    private String overview;
    private String poster_path;
    private String release_date;
    private String vote_average;
    private String vote_count;
    private String title;

    public MovieDetail() {
    }

    public MovieDetail(String titleName) {
        title = titleName;
    }

    private ArrayList<GenresList> genres;
    private ReleaseDate release_dates;
    private Videos videos ;
    private Reviews reviews ;

    public Videos getVideos() {
        return videos;
    }

    public Reviews getReviews() {
        return reviews;
    }

    public String getBackdrop_path() {
        return IMAGE_BASE_URL + backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getPoster_path() {
        return IMAGE_BASE_URL + poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getVote_count() {
        return vote_count;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<GenresList> getGenres() {
        return genres;
    }

    public ReleaseDate getRelease_dates() {
        return release_dates;
    }

    public class GenresList {
        private String name;

        public String getName() {
            return name;
        }
    }

    public class ReleaseDate {
        private ArrayList<Results> results;

        public ArrayList<Results> getResults() {
            return results;
        }

        public class Results {
            private String iso_3166_1;
            private ArrayList<Certification> release_dates;

            public String getIso_3166_1() {
                return iso_3166_1;
            }

            public ArrayList<Certification> getRelease_dates() {
                return release_dates;
            }

            public class Certification {
                public String getCertification() {
                    return certification;
                }

                private String certification;
            }
        }
    }

    public class Videos {
        private ArrayList<CategoryMovieResult> results;

        public ArrayList<CategoryMovieResult> getVideos() {
            return results;
        }
    }

    public class Reviews {
        private ArrayList<CategoryMovieResult> results;

        public ArrayList<CategoryMovieResult> getReviews() {
            return results;
        }
    }

}
