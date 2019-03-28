package com.example.hares.movies.MovieDetail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hares.movies.R;
import com.example.hares.movies.Retrofit.CategoryMovieResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<CategoryMovieResult> mData;
    private List<String> mAuthor;
    private List<String> mContent;
    private boolean isOffline = false;

    // this constructor used when connection is exist
    ReviewAdapter(ArrayList<CategoryMovieResult> list) {
        mData = list;
    }

    // this constructor used when app connection down
    ReviewAdapter(List<String> author, List<String> content) {
        mAuthor = author;
        mContent = content;
        isOffline = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();

        // return new viewHolder That hold view for each list items
        LayoutInflater inflater = LayoutInflater.from(context);
        int layout = R.layout.reviews_item;
        View view = inflater.inflate(layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (!isOffline) {

            viewHolder.mAuthorName.setText(mData.get(i).getAuthor());
            viewHolder.mContent.setText(mData.get(i).getContent());
            // indicate to user to know there is review if he want to read
            reviewReadIndication(viewHolder);

        } else if( mAuthor.size() > 0 ) {

            viewHolder.mAuthorName.setText(mAuthor.get(i));
            viewHolder.mContent.setText(mContent.get(i));

            // indicate to user to know there is review if he want to read
            reviewReadIndication(viewHolder);
        }
        viewHolder.mReviewCard.setOnClickListener(view -> {
            if (viewHolder.mContent.getVisibility() == View.VISIBLE) {
                viewHolder.mContent.setVisibility(GONE);
                viewHolder.mupArrow.setVisibility(GONE);
                viewHolder.mDownArrow.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mContent.setVisibility(View.VISIBLE);
                viewHolder.mupArrow.setVisibility(View.VISIBLE);
                viewHolder.mDownArrow.setVisibility(GONE);
            }
        });
    }

    private void reviewReadIndication (@NonNull ViewHolder viewHolder) {
        viewHolder.mDownArrow.setVisibility(View.VISIBLE);
        viewHolder.mupArrow.setVisibility(GONE);
    }

    @Override
    public int getItemCount() {
        if (isOffline && mAuthor.size() > 0) return mAuthor.size();
        else return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.up_arrow)
        ImageView mupArrow;
        @BindView(R.id.down_arrow)
        ImageView mDownArrow;
        @BindView(R.id.author_name)
        TextView mAuthorName;
        @BindView(R.id.content)
        TextView mContent;
        @BindView(R.id.review_card)
        CardView mReviewCard;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }
    }
}

