package com.example.hares.movies.MoviesHomePage;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hares.movies.MovieDetail.SelectedMovieDetail;
import com.example.hares.movies.R;
import com.example.hares.movies.Retrofit.CategoryMovieResult;
import com.example.hares.movies.SelectedListMovies.CategorizedList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hares.movies.MoviesHomePage.MoviesActivity.*;
import static com.example.hares.movies.SelectedListMovies.PopularGenresList.*;


public class DetailInfoAdapter extends RecyclerView.Adapter<DetailInfoAdapter.ViewHolder> {

    private final static String TAG = "DetailInfoAdapter";
    public final static String LIST_NAME = "genresList";

    private Context mContext;
    private ArrayList<CategoryMovieResult> mData;
    private String mSender = null;
    private List<String> mNameList = new ArrayList<>();
    private List<byte[]> pic = new ArrayList<>();
    private boolean isFavoriteListRequest = false;

    public DetailInfoAdapter(Context context) {
        mContext = context;
    }


    public void setData(ArrayList<CategoryMovieResult> list, String sender) {

        mData = list;
        mSender = sender;

    }

    public void setData(List<String> mSimilarMovieName, List<byte[]> mSimilarMoviePoster, String sender) {
        this.mSender = sender;
        isFavoriteListRequest = true;

        for (int i = 0; i < mSimilarMovieName.size(); i++) {
            this.mNameList.add(mSimilarMovieName.get(i));
            this.pic.add(mSimilarMoviePoster.get(i));
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Context context = parent.getContext();
        // return new viewHolder That hold view for each list items
        LayoutInflater inflater = LayoutInflater.from(context);
        int layout = R.layout.movies_item_detail;
        View view = inflater.inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {

        String imageUrl = null;
        String name;
        if (mData != null) {
            holder.id = Integer.valueOf(mData.get(i).getId());

            switch (mSender) {
                case LIST_NAME:
                    imageUrl = posterPath.get(i);
                    name = mData.get(i).getName();
                    break;
                case "FavoriteList":
                    name = mData.get(i).getTitle();
                    break;
                default:
                    imageUrl = mData.get(i).getPoster_path();
                    name = mData.get(i).getTitle();
                    break;
            }

            //to make user know it's not complete name
            if (name.length() > 17) name = name.substring(0, 15) + "...";

            holder.mMovieNameTextView.setText(name);

            if (mSender.equals(mContext.getString(R.string.FavoriteList_sender)))
                holder.mMoviePosterImageView.setImageBitmap(mData.get(i).getImage());
            else if (mSender.equals(mContext.getString(R.string.SimilarList))) {

                Picasso.get().load(imageUrl).into(holder.mMoviePosterImageView);
            } else {
                Picasso.get().load(imageUrl).into(holder.mMoviePosterImageView);

            }

            if (MoviesActivity.FAVORITE_LIST_IDS.contains(holder.id)) {

                holder.mFavoriteSign.setVisibility(View.VISIBLE);
            } else holder.mFavoriteSign.setVisibility(View.GONE);
        } else {
            String getName = mNameList.get(i);

            if (getName.length() > 17) getName = getName.substring(0, 15) + "...";
            holder.mMovieNameTextView.setText(getName);
            Bitmap getImage = BitmapFactory.decodeByteArray(pic.get(i), 0, pic.get(i).length);
            holder.mMoviePosterImageView.setImageBitmap(getImage);
        }


        holder.mMoviePosterImageView.setOnClickListener(new View.OnClickListener() {
            Class destination;
            Intent message;

            @Override
            public void onClick(View view) {
                if (!mSender.equals(LIST_NAME)) {
                    destination = SelectedMovieDetail.class;
                    message = new Intent(mContext, destination);
                    message.putExtra("movieId", holder.id);
                    if(mSender.equals(mContext.getString(R.string.FavoriteList_sender)))
                    message.putExtra("favorite list",mContext.getString(R.string.FavoriteList_sender));
                } else {
                    destination = CategorizedList.class;
                    message = new Intent(mContext, destination);
                    message.putExtra(SENDER_NAME, DetailInfoAdapter.TAG);
                    message.putExtra(GENRES_ID_KEY, holder.id + "");
                }

                if (checkNetwork(mContext)) mContext.startActivity(message);
                else if (mSender.equals(mContext.getString(R.string.FavoriteList_sender))) mContext.startActivity(message);

                else Toast.makeText(mContext, R.string.ToastErrorMessage, Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    public int getItemCount() {
        if (mSender.equals(mContext.getString(R.string.FavoriteList_sender))) return mData.size();
        if (!isFavoriteListRequest && !mSender.equals(mContext.getString(R.string.FavoriteList_sender)))
            return mData.size() / 2;
        else return this.mNameList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_name)
        TextView mMovieNameTextView;
        @BindView(R.id.movie_image)
        ImageView mMoviePosterImageView;
        @BindView(R.id.favorite_sign)
        ImageView mFavoriteSign;
        private int id = 0;

        public ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
