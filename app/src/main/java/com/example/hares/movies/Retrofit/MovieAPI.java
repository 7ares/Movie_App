package com.example.hares.movies.Retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieAPI {

    @GET("trending/movie/week?")
    Call<CategoryMovieList> getLatestMovie();

    @GET("trending/movie/week?")
    Call<CategoryMovieList> getLatestMovie(@Query(value = "sort_by", encoded = true) String sortBy);

    @GET("movie/upcoming?")
    Call<CategoryMovieList> getUpcomingMovieList();

    @GET("movie/upcoming?")
    Call<CategoryMovieList> getUpcomingMovieList(@Query(value = "sort_by", encoded = true) String sortBy);

    @GET("movie/popular?")
    Call<CategoryMovieList> getMostPopularMovies();

    @GET("movie/popular?")
    Call<CategoryMovieList> getMostPopularMovies(@Query(value = "sort_by", encoded = true) String sortBy);

    @GET("movie/top_rated?")
    Call<CategoryMovieList> getTopRatedMovies();

    @GET("movie/top_rated?")
    Call<CategoryMovieList> getTopRatedMovies(@Query(value = "sort_by", encoded = true) String sortBy);

    @GET("discover/movie?")
    Call<CategoryMovieList> getMoviesInTheater(@Query(value = "primary_release_date.gte", encoded = true) String startDate, @Query(value = "primary_release_date.lte", encoded = true) String endDate);

    @GET("discover/movie?")
    Call<CategoryMovieList> getMoviesInTheater(@Query(value = "primary_release_date.gte", encoded = true) String startDate, @Query(value = "primary_release_date.lte", encoded = true) String endDate, @Query(value = "sort_by", encoded = true) String sortBy);

    @GET("search/movie?")
    Call<CategoryMovieList> doSearch(@Query(value = "query", encoded = true) String query);

    @GET("movie/{id}?append_to_response=release_dates,videos,reviews")
    Call<MovieDetail> getMovieDetail(@Path("id") String id);

    @GET("discover/movie?")
    Call<CategoryMovieList> getMovieByGenres(@Query(value = "with_genres", encoded = true) String genresId, @Query(value = "sort_by", encoded = true) String sortBy);

    @GET("movie/{id}/similar?")
    Call<CategoryMovieList> getSimilarMovies(@Path("id") String id);

    @GET("genre/movie/list?")
    Call<CategoryMovieList> getGenresList();

    // https://www.youtube.com/watch?v=SUXWAEX2jlg
}
