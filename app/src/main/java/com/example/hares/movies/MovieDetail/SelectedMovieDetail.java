package com.example.hares.movies.MovieDetail;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.hares.movies.DataBase.DataBaseHelper;
import com.example.hares.movies.DataBase.ExecutorHandler;
import com.example.hares.movies.DataBase.FavoriteMovieDetail;
import com.example.hares.movies.DataBase.ReviewsDetail;
import com.example.hares.movies.DataBase.SimilarMoviesDetail;
import com.example.hares.movies.MoviesHomePage.DetailInfoAdapter;
import com.example.hares.movies.MoviesHomePage.MoviesActivity;
import com.example.hares.movies.R;
import com.example.hares.movies.Retrofit.CategoryMovieList;
import com.example.hares.movies.Retrofit.CategoryMovieResult;
import com.example.hares.movies.Retrofit.MovieAPI;
import com.example.hares.movies.Retrofit.MovieDetail;
import com.example.hares.movies.Retrofit.MovieDetail.ReleaseDate;
import com.example.hares.movies.Retrofit.MovieDetail.ReleaseDate.Results.Certification;
import com.example.hares.movies.databinding.ActivitySelectedMovieDetailBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static com.example.hares.movies.MoviesHomePage.MoviesActivity.*;

public class SelectedMovieDetail extends AppCompatActivity {
    public static final String TAG = "SelectedMovieDetail";
    ActivitySelectedMovieDetailBinding mBinder;

    private DetailInfoAdapter mAdapter = new DetailInfoAdapter(this);

    private ReviewAdapter mReviewAdapter;
    private DataBaseHelper mDBH;

    // check if selected movie is already in favorite list
    private Boolean isThisFavoriteMovie = false;

    // parameters that will be used in dataBase and populate our view through it
    private String mMovieName, mMovieOverView, mMovieVoteAverage, mMovieReleaseYear, mMovieCertification, mVoteCont, mMovieGenres;
    private byte[] mMovieImage, mMovieDropBack;

    // parameters of child tables that are relate to main movie like each movie has it's own review and similar movies
    public static List<String> mReviewContent, mReviewAuthor, mSimilarMovieName, mSimilarMoviePicsUrl;
    public static List<byte[]> mSimilarMoviePoster;

    private String mMovieId, mVideoId , mSender ;

    // result of add or delete database process
    private long id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_movie_detail);

        mReviewAuthor = new ArrayList<>();
        mReviewContent = new ArrayList<>();

        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_selected_movie_detail);
        mDBH = DataBaseHelper.getInstance(getApplicationContext());

        // create array with size 10 item to can handel return result of picasso due to it
        // do it's work on other thread so i get output not as same order of input
        //  because of that i create this array to put the result in order that i want
        mSimilarMoviePoster = new ArrayList<>(10);

        // setup (Recycler)Similar list movie
        LinearLayoutManager layoutManager;
        GridLayoutManager gridLayoutManager;
        // handel tablet UI
        if (mBinder.tabletLayout != null) {
            gridLayoutManager = new GridLayoutManager(this, 1);
            mBinder.moreLikeMovieList.setHasFixedSize(true);
            mBinder.moreLikeMovieList.setLayoutManager(gridLayoutManager);
        }
        // handel phone UI
        else {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mBinder.moreLikeMovieList.setHasFixedSize(true);
            mBinder.moreLikeMovieList.setLayoutManager(layoutManager);
        }


        // setup (RecyclerView) Review List
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinder.reviewsList.setHasFixedSize(true);
        mBinder.reviewsList.setLayoutManager(reviewLayoutManager);

        // get id of selected movie
        mMovieId = getIntent().getIntExtra("movieId", 0) + "";

        // check if this movie is already in favorite list
        if (FAVORITE_LIST_IDS.size() > 0 && FAVORITE_LIST_IDS.contains(Integer.valueOf(mMovieId))) {

            // if the connectivity down and this movie is saved in favorite list so we can fetch the data from data base
            if (!checkNetwork(this)) {

                // here we get detail about the movie from data base
                LiveData<FavoriteMovieDetail> movie = mDBH.myDAO().getMovie(Integer.valueOf(mMovieId));
                movie.observe(this, favoriteMovieDetail -> {

                    if (favoriteMovieDetail != null) {


                        mBinder.playButton.setVisibility(GONE);
                        mBinder.trailerName.setVisibility(GONE);

                        mBinder.movieTrailer.setImageBitmap(getImage(favoriteMovieDetail.MOVIE_BACKDROP));
                        mBinder.imageOfSelectedMovie.setImageBitmap(getImage(favoriteMovieDetail.MOVIE_POSTER));

                        mBinder.nameOfSelectedMovie.setText(favoriteMovieDetail.MOVIE_NAME);
                        mBinder.releaseDateOfSelectedMovie.setText(favoriteMovieDetail.MOVIE_RELEASE_YEAR);
                        mBinder.classificationOfSelectedMovie.setText(favoriteMovieDetail.MOVIE_US_CERTIFICATION);
                        mBinder.genresOfSelectedMovie.setText(favoriteMovieDetail.MOVIE_GENRES);
                        mBinder.overview.setText(favoriteMovieDetail.MOVIE_OVERVIEW);
                        mBinder.voteAverage.setText(favoriteMovieDetail.MOVIE_VOTE_COUNT);
                        mBinder.voteCount.setText(favoriteMovieDetail.MOVIE_VOTE_AVERAGE);
                    }
                });

                // here we get similar movie list of the selected movie
                LiveData<List<SimilarMoviesDetail>> similarMovieList = mDBH.similarMovieDAO().readListOfMovie(Integer.valueOf(mMovieId));
                similarMovieList.observe(this, similarMoviesDetails -> {
                    if (similarMoviesDetails != null) {
                        if (similarMoviesDetails.size() > 0) {
                            mSimilarMovieName = new ArrayList<>();
                            mSimilarMoviePoster = new ArrayList<>();


                            // get data from table and save it in Arrays
                            for (int i = 0; i < similarMoviesDetails.size(); i++) {
                                mSimilarMovieName.add(similarMoviesDetails.get(i).SIMILAR_MOVIE_NAME);
                                mSimilarMoviePoster.add(similarMoviesDetails.get(i).SIMILAR_MOVIE_PICS);

                            }

                            // send ArraysList to Detail Adapter with name to can handle it depending on that name
                            mAdapter.setData(mSimilarMovieName, mSimilarMoviePoster, getString(R.string.DataBase_Sender));

                            mBinder.moreLikeMovieList.setAdapter(mAdapter);

                            mBinder.moreLikeMovieList.setVisibility(View.VISIBLE);
                        }
                        // we display message for user that movie doesn't has similar movie to prevent him to think it's error in my app
                        // we must do it because he used to see similar list of movie
                        else {
                            mBinder.similarMovie.setText(R.string.no_similarity);
                            mBinder.moreLikeMovieList.setVisibility(GONE);
                        }
                    }
                });

                LiveData<List<ReviewsDetail>> reviewList = mDBH.reviewsDAO().readListOfReviews(Integer.valueOf(mMovieId));
                reviewList.observe(this, reviewsDetails -> {

                    if (reviewsDetails != null && reviewsDetails.size() > 0) {
                        mReviewContent = new ArrayList<>();
                        mReviewAuthor = new ArrayList<>();

                        for (int i = 0; i < reviewsDetails.size(); i++) {
                            mReviewAuthor.add(reviewsDetails.get(i).REVIEWS_AUTHOR);
                            mReviewContent.add(reviewsDetails.get(i).REVIEWS_CONTENT);
                        }

                        mReviewAdapter = new ReviewAdapter(mReviewAuthor, mReviewContent);
                        mBinder.reviewsList.setAdapter(mReviewAdapter);

                    } else {
                        mBinder.reviewsList.setVisibility(GONE);
                        mBinder.reviewsText.setText(R.string.empty_review);
                    }

                });
            }
            // add sign that the movie is already in favorite list to make user know before he click
            // weather if he will add or remove it
            checkFavoriteList(Integer.valueOf(mMovieId));
        }

// handle adding and removing process to and from dataBase
        mBinder.watchLater.setOnClickListener(view -> {
            if (!checkNetwork(this)) {
                Toast.makeText(this, "Connection Down Try Again Later", Toast.LENGTH_LONG).show();
            } else {
                mSender = getIntent().getStringExtra("favorite list");

                FavoriteMovieDetail favoriteMovieDetail
                        = new FavoriteMovieDetail(mMovieId, mMovieName, mVoteCont, mMovieVoteAverage, mMovieImage
                        , mMovieDropBack, mMovieReleaseYear, mMovieCertification, mMovieOverView, mMovieGenres);

                ExecutorHandler.getInstance().diskIO().execute(() -> {
                    //id is return result of process
                    // if movie not in favorite list so add it
                    if (!isThisFavoriteMovie) {
                        // add movie detail
                        id = mDBH.myDAO().addMovie(favoriteMovieDetail);
                        runOnMainThread();

                        // add similar movie list
                        if (mSimilarMovieName.size() > 0) {
                            mSimilarMoviePoster = saveSimilarList();
                            int size;
                            if (mSimilarMovieName.size() > 10) size = 10;
                            else size = mSimilarMovieName.size();
                            for (int i = 0; i < size; i++) {
                                SimilarMoviesDetail similarMoviesDetail = new SimilarMoviesDetail(Integer.valueOf(mMovieId), mSimilarMovieName.get(i), mSimilarMoviePoster.get(i));
                                mDBH.similarMovieDAO().addSimilarMovie(similarMoviesDetail);
                            }
                        }

                        if (mReviewAuthor != null && mReviewAuthor.size() > 0) {
                            for (int i = 0; i < mReviewAuthor.size(); i++) {
                                ReviewsDetail reviewsDetail = new ReviewsDetail(Integer.valueOf(mMovieId), mReviewAuthor.get(i), mReviewContent.get(i));
                                mDBH.reviewsDAO().addReview(reviewsDetail);
                            }
                        }
                    } else {
                        // if movie is already in favorite list so delete it
                        id = (long) mDBH.myDAO().deleteMovie(favoriteMovieDetail);
                        runOnMainThread();
                    }
                });
            }
        });
        // play movie trailer
        mBinder.playButton.setOnClickListener(view -> {
            if (mVideoId.length() > 0) watchYoutubeVideo(mVideoId);
        });
        mBinder.shareTrailerLink.setOnClickListener(v -> shareTrailerLink(getString(R.string.YouTubeLinke) + mVideoId));

        if (checkNetwork(this)) {

            MovieAPI movieAPI = MoviesActivity.movieAPI;

            Call<MovieDetail> getDetailOfMovie = movieAPI.getMovieDetail(mMovieId);

            final Call<CategoryMovieList> getSimilarMovies = movieAPI.getSimilarMovies(mMovieId);

            getDetailOfMovie.enqueue(new Callback<MovieDetail>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<MovieDetail> call, @NonNull Response<MovieDetail> response) {
                    MovieDetail movieDetail = response.body();

                    MovieDetail.Reviews reviews = movieDetail != null ? movieDetail.getReviews() : null;
                    ArrayList<CategoryMovieResult> reviewsList = reviews != null ? reviews.getReviews() : null;

                    if (reviewsList == null || reviewsList.size() == 0) {

                        mBinder.reviewsList.setVisibility(GONE);
                        mBinder.reviewsText.setText(R.string.empty_review);
                    } else {

                        for (int i = 0; i < reviewsList.size(); i++) {
                            mReviewAuthor.add(reviewsList.get(i).getAuthor());
                            mReviewContent.add(reviewsList.get(i).getContent());
                        }
                        mReviewAdapter = new ReviewAdapter(reviewsList);
                        mBinder.reviewsList.setAdapter(mReviewAdapter);
                    }

                    MovieDetail.Videos videos = null;
                    if (movieDetail != null) {
                        videos = movieDetail.getVideos();
                    }

                    ArrayList<CategoryMovieResult> videosList = videos != null ? videos.getVideos() : null;
                    if (videosList != null && videosList.size() > 0) {
                        mBinder.trailerName.setText(videosList.get(0).getName());
                        mVideoId = videosList.get(0).getKey();
                        mBinder.playButton.setVisibility(View.VISIBLE);
                    } else {
                        mBinder.shareTrailerLink.setVisibility(GONE);
                        mBinder.playButton.setVisibility(GONE);
                    }

                    String backDrop = movieDetail != null ? movieDetail.getBackdrop_path() : null;
                    if (backDrop != null && backDrop.endsWith("null"))
                        mBinder.movieTrailer.setImageResource(R.drawable.no_photo_found);
                    else
                        Picasso.get().load(backDrop).into(mBinder.movieTrailer, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Drawable drawable = mBinder.movieTrailer.getDrawable();
                                if (drawable != null) mMovieDropBack = convertImage(drawable);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });

                    String poster = movieDetail != null ? movieDetail.getPoster_path() : null;
                    if (poster != null && poster.endsWith("null"))
                        mBinder.imageOfSelectedMovie.setImageResource(R.drawable.no_photo_found);
                    else
                        Picasso.get().load(poster).into(mBinder.imageOfSelectedMovie, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Drawable a = mBinder.imageOfSelectedMovie.getDrawable();
                                if (a != null) mMovieImage = convertImage(a);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });

                    String summary = movieDetail != null ? movieDetail.getOverview() : null;
                    if ((summary != null ? summary.length() : 0) > 0)
                        mBinder.overview.setText(summary);
                    else mBinder.overview.setText(R.string.overViewMessage);

                    mMovieOverView = mBinder.overview.getText().toString();

                    String certification = "NR";
                    ReleaseDate releaseDate = movieDetail != null ? movieDetail.getRelease_dates() : null;

                    ArrayList<ReleaseDate.Results> results = releaseDate != null ? releaseDate.getResults() : null;
                    String countryCertification = null;
                    for (int i = 0; i < (results != null ? results.size() : 0); i++) {
                        countryCertification = results.get(i).getIso_3166_1();
                        if (countryCertification.equals("US")) {
                            ArrayList<Certification> certif = results.get(i).getRelease_dates();
                            String getCertification = certif.get(0).getCertification();
                            if (getCertification.length() > 0) certification = getCertification;
                            break;
                        }
                    }
                    mBinder.classificationOfSelectedMovie.setText(certification);
                    mMovieCertification = mBinder.classificationOfSelectedMovie.getText().toString();

                    ArrayList<MovieDetail.GenresList> genresList = movieDetail != null ? movieDetail.getGenres() : null;


                    for (int i = 0; i < (genresList != null ? genresList.size() : 0); i++)
                        mMovieGenres = getGenresList(genresList);
                    mBinder.genresOfSelectedMovie.setText(mMovieGenres);

                    if ((movieDetail != null ? movieDetail.getRelease_date() : null) != null && movieDetail.getRelease_date().length() > 0) {
                        String releaseYear = movieDetail.getRelease_date().substring(0, 4);
                        mBinder.releaseDateOfSelectedMovie.setText(releaseYear);
                        mMovieReleaseYear = mBinder.releaseDateOfSelectedMovie.getText().toString();
                    }

                    mMovieVoteAverage = movieDetail != null ? movieDetail.getVote_average() : null;
                    mBinder.voteAverage.setText(mMovieVoteAverage);

                    mBinder.voteCount.setText((movieDetail != null ? movieDetail.getVote_count() : null) + getString(R.string.count));
                    mVoteCont = mBinder.voteCount.getText().toString();

                    mMovieName = movieDetail != null ? movieDetail.getTitle() : null;
                    mBinder.nameOfSelectedMovie.setText(mMovieName);

                    getSimilarMovies.clone().enqueue(new Callback<CategoryMovieList>() {
                        @Override
                        public void onResponse(@NonNull Call<CategoryMovieList> call, @NonNull Response<CategoryMovieList> response) {

                            CategoryMovieList similarList = response.body();
                            ArrayList<CategoryMovieResult> list = similarList != null ? similarList.getCategoryMovieResults() : null;

                            mSimilarMovieName = new ArrayList<>();
                            mSimilarMoviePicsUrl = new ArrayList<>();


                            if ((list != null ? list.size() : 0) > 0) {
                                mAdapter.setData(list, getString(R.string.SimilarList));

                                mBinder.moreLikeMovieList.setAdapter(mAdapter);

                                mBinder.moreLikeMovieList.setVisibility(View.VISIBLE);
                                for (int i = 0; i < list.size(); i++) {
                                    mSimilarMovieName.add(list.get(i).getTitle());
                                    mSimilarMoviePicsUrl.add(list.get(i).getPoster_path());
                                    prepareSimilarMovie(i, list);
                                    }
                                    }
                            else {
                                mBinder.similarMovie.setVisibility(GONE);
                                mBinder.moreLikeMovieList.setVisibility(GONE);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<CategoryMovieList> call, @NonNull Throwable t) {
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<MovieDetail> call, @NonNull Throwable t) {
                }
            });
        }
    }

    private void runOnMainThread() {
        runOnUiThread(() -> {
            if (id > 0) {
                checkFavoriteList((int) id);
            }
        });
    }

    @NonNull
    private String getGenresList(ArrayList<MovieDetail.GenresList> genresList) {
        StringBuilder genres = null;
        if (genresList.size() > 0) {
            for (int i = 0; i < genresList.size(); i++) {
                if (i > 2) break;
                else {
                    if (i == 0)
                        genres = new StringBuilder(genresList.get(i).getName());
                    else
                        genres.append(" , ").append(genresList.get(i).getName());
                }
            }
        } else genres = new StringBuilder("Unknown");
        return genres.toString();
    }

    private void checkFavoriteList(int resultId) {
        switch (resultId) {
            case 1:

                mBinder.sign.setVisibility(GONE);
                mBinder.watchLater.setText(R.string.add_to_favorite_list);
                mBinder.checkSign.setVisibility(GONE);
                isThisFavoriteMovie = false;

                break;
            default:

                mBinder.sign.setVisibility(View.VISIBLE);
                mBinder.watchLater.setText(R.string.already_in_favorite_list);
                mBinder.checkSign.setVisibility(View.VISIBLE);
                isThisFavoriteMovie = true;

                break;

        }

    }


    private void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        startActivity(appIntent);
    }

    public static byte[] convertImage(Drawable drawable) {
        BitmapDrawable src = (BitmapDrawable) drawable;
        Bitmap bitmap = src.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);


        return stream.toByteArray();

    }

    private void prepareSimilarMovie(int index, ArrayList<CategoryMovieResult> list) {
        switch (index) {
            case 0:
                Picasso.get().load(list.get(index).getPoster_path()).into(mBinder.placeHolder0);
                break;
            case 1:
                Picasso.get().load(list.get(index).getPoster_path()).into(mBinder.placeHolder1);
                break;
            case 2:
                Picasso.get().load(list.get(index).getPoster_path()).into(mBinder.placeHolder2);
                break;
            case 3:
                Picasso.get().load(list.get(index).getPoster_path()).into(mBinder.placeHolder3);
                break;
            case 4:
                Picasso.get().load(list.get(index).getPoster_path()).into(mBinder.placeHolder4);
                break;
            case 5:
                Picasso.get().load(list.get(index).getPoster_path()).into(mBinder.placeHolder5);
                break;
            case 6:
                Picasso.get().load(list.get(index).getPoster_path()).into(mBinder.placeHolder6);
                break;
            case 7:
                Picasso.get().load(list.get(index).getPoster_path()).into(mBinder.placeHolder7);
                break;
            case 8:
                Picasso.get().load(list.get(index).getPoster_path()).into(mBinder.placeHolder8);
                break;
            case 9:
                Picasso.get().load(list.get(index).getPoster_path()).into(mBinder.placeHolder9);
                break;
        }
    }

    private List<byte[]> saveSimilarList() {
        List<byte[]> list = new ArrayList<>();
        list.add(convertImage(mBinder.placeHolder0.getDrawable()));
        list.add(convertImage(mBinder.placeHolder1.getDrawable()));
        list.add(convertImage(mBinder.placeHolder2.getDrawable()));
        list.add(convertImage(mBinder.placeHolder3.getDrawable()));
        list.add(convertImage(mBinder.placeHolder4.getDrawable()));
        list.add(convertImage(mBinder.placeHolder5.getDrawable()));
        list.add(convertImage(mBinder.placeHolder6.getDrawable()));
        list.add(convertImage(mBinder.placeHolder7.getDrawable()));
        list.add(convertImage(mBinder.placeHolder8.getDrawable()));
        list.add(convertImage(mBinder.placeHolder9.getDrawable()));
        return list;

    }

    private void shareTrailerLink(String link) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);

        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mSender!= null && mSender.equals(getString(R.string.FavoriteList_sender))){
            Intent openMainActivity = new Intent(this , MoviesActivity.class);
            startActivity(openMainActivity);
        }
    }
}

