package com.example.hares.movies.SelectedListMovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hares.movies.MovieDetail.SelectedMovieDetail;
import com.example.hares.movies.MoviesHomePage.MoviesActivity;
import com.example.hares.movies.R;
import com.example.hares.movies.Retrofit.CategoryMovieResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategorizedListAdapter extends RecyclerView.Adapter<CategorizedListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<CategoryMovieResult> mData;

    CategorizedListAdapter(Context context, ArrayList<CategoryMovieResult> list) {
        mData = list;
        mContext = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        int layout = R.layout.categorized_list_items;

        View view = inflater.inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        double voteAverage = Double.valueOf(mData.get(i).getVote_average());

        String vote = String.valueOf(voteAverage);
        if (vote.endsWith("0")) vote = vote.substring(0, 1);

        Picasso.get().load(mData.get(i).getPoster_path()).into(holder.mMovieImageInCategorizedList);

        holder.mVoteAverageOfMovieInCategorizedList.setText(vote);

        holder.movieId = Integer.valueOf(mData.get(i).getId());
        setStarRate(voteAverage, holder);

        if(MoviesActivity.FAVORITE_LIST_IDS.contains(holder.movieId)){
            holder.mFavoriteSign.setVisibility(View.VISIBLE);
            }
            else holder.mFavoriteSign.setVisibility(View.GONE);

        holder.mMovieImageInCategorizedList.setOnClickListener(view -> {
            Class destination = SelectedMovieDetail.class;
            Intent message = new Intent(mContext, destination);
            message.putExtra("movieId", holder.movieId);

            if (MoviesActivity.checkNetwork(mContext)) mContext.startActivity(message);
            else Toast.makeText(mContext, "no network connection", Toast.LENGTH_LONG).show();
            });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_rate_1)
        ImageView mRate1 ;
        @BindView(R.id.movie_rate_2)
        ImageView mRate2 ;
        @BindView(R.id.movie_rate_3)
        ImageView mRate3 ;
        @BindView(R.id.movie_rate_4)
        ImageView mRate4 ;
        @BindView(R.id.movie_rate_5)
        ImageView mRate5 ;
        @BindView(R.id.vote_average)
        TextView mVoteAverageOfMovieInCategorizedList;
        @BindView(R.id.image_of_movie_in_list)
        ImageView mMovieImageInCategorizedList;
        @BindView(R.id.favorite_sign)
        ImageView   mFavoriteSign ;

        private int movieId;

        public ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this,view);



        }
    }

    private void setStarRate(double voteAverage, ViewHolder holder) {
        if (voteAverage > 9.5) {
            holder.mRate1.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate2.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate3.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate4.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate5.setImageResource(R.drawable.ic_yellow_rate_stare);
        } else if (voteAverage > 8.5) {
            holder.mRate1.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate2.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate3.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate4.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate5.setImageResource(R.drawable.ic_twotone_star_half_24px);
        } else if (voteAverage > 7.5) {
            holder.mRate1.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate2.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate3.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate4.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate5.setImageResource(R.drawable.ic_rate_stare);
        } else if (voteAverage > 6.5) {
            holder.mRate1.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate2.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate3.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate4.setImageResource(R.drawable.ic_twotone_star_half_24px);
            holder.mRate5.setImageResource(R.drawable.ic_rate_stare);
        } else if (voteAverage > 5.5) {
            holder.mRate1.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate2.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate3.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate4.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate5.setImageResource(R.drawable.ic_rate_stare);
        } else if (voteAverage > 4.5) {
            holder.mRate1.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate2.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate3.setImageResource(R.drawable.ic_twotone_star_half_24px);
            holder.mRate4.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate5.setImageResource(R.drawable.ic_rate_stare);
        } else if (voteAverage > 3.5) {
            holder.mRate1.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate2.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate3.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate4.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate5.setImageResource(R.drawable.ic_rate_stare);
        } else if (voteAverage > 2.5) {
            holder.mRate1.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate2.setImageResource(R.drawable.ic_twotone_star_half_24px);
            holder.mRate3.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate4.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate5.setImageResource(R.drawable.ic_rate_stare);
        } else if (voteAverage > 1.5) {
            holder.mRate1.setImageResource(R.drawable.ic_yellow_rate_stare);
            holder.mRate2.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate3.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate4.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate5.setImageResource(R.drawable.ic_rate_stare);
        } else if (voteAverage < 1.5) {
            holder.mRate1.setImageResource(R.drawable.ic_twotone_star_half_24px);
            if (voteAverage == 0) holder.mRate1.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate2.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate3.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate4.setImageResource(R.drawable.ic_rate_stare);
            holder.mRate5.setImageResource(R.drawable.ic_rate_stare);
        }


    }

}
