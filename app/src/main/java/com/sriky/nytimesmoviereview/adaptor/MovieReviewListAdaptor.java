package com.sriky.nytimesmoviereview.adaptor;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.sriky.nytimesmoviereview.R;
import com.sriky.nytimesmoviereview.data.model.Multimedia;
import com.sriky.nytimesmoviereview.data.model.Result;
import com.sriky.nytimesmoviereview.databinding.MovieReviewListItemBinding;
import com.sriky.nytimesmoviereview.utils.MovieReviewUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class MovieReviewListAdaptor
        extends RecyclerView.Adapter<MovieReviewListAdaptor.MovieReviewViewHolder> {

    private static final String JPEG = ".jpg";
    private static final String PNG = ".png";
    private static final String PUBLICATION_DATE_FORMAT = "yyyy-MM-dd";
    private static final String REVIEW = "Review:";

    private Context mContext;
    private List<Result> mReviewList;

    public MovieReviewListAdaptor(Context context) {
        mContext = context;
    }

    /**
     * Update the list of reviews to the supplied list.
     *
     * @param results The new list of reviews to be displayed.
     */
    public void updateReviewList(List<Result> results) {
        mReviewList = results;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new MovieReviewViewHolder((MovieReviewListItemBinding) DataBindingUtil.inflate(
                layoutInflater, R.layout.movie_review_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewViewHolder holder, int position) {
        if (mReviewList != null && position < mReviewList.size()) {
            Result review = mReviewList.get(position);
            if (review != null) {
                bindViews(holder.movieReviewListItemBinding, review);
            }
        }
    }

    /**
     * Binds the views with the data from {@link Result} object.
     *
     * @param movieReviewListItemBinding The {@link MovieReviewListItemBinding} associate for the item.
     * @param review                     The {@link Result} object.
     */
    private void bindViews(MovieReviewListItemBinding movieReviewListItemBinding, Result review) {
        // set title
        String title = review.getDisplayTitle();
        movieReviewListItemBinding.tvMovieTitle.setText(title);

        // set ratings
        movieReviewListItemBinding.tvRating.setText(review.getMpaaRating());

        // set the movie thumbnail
        Multimedia multimedia = review.getMultimedia();
        if (multimedia != null) {
            String source = multimedia.getSrc();
            // validate source and display only jpg/png images.
            if (!TextUtils.isEmpty(source)
                    && (source.endsWith(JPEG) || source.endsWith(PNG))) {
                Picasso.with(mContext)
                        .load(Uri.parse(source))
                        .placeholder(R.color.primaryLightColor)
                        .error(R.drawable.ic_error)
                        .into(movieReviewListItemBinding.ivThumbnail);

                //a11y support
                movieReviewListItemBinding.ivThumbnail
                        .setContentDescription(title);

            }
        }

        // set reviewer
        movieReviewListItemBinding.tvReviewer.setText(review.getByline());

        // set review date relative to current date, i.e. xd or xh or xm
        try {
            DateFormat format = new SimpleDateFormat(PUBLICATION_DATE_FORMAT, Locale.getDefault());
            Date date = format.parse(review.getPublicationDate());
            movieReviewListItemBinding.tvPublicationDate
                    .setText(MovieReviewUtils.getFormattedDateFromNow(mContext, date.getTime()));
        } catch (ParseException e) {
            movieReviewListItemBinding.tvPublicationDate.setText(review.getPublicationDate());
            Timber.e("Error while parsing date for: %s, displaying date as is for now!", title);
            e.printStackTrace();
        }

        // set the review headline
        String headline = review.getHeadline();
        headline = headline.startsWith(REVIEW) ?
                headline.substring(headline.indexOf(REVIEW) + REVIEW.length() + 1) : headline;
        movieReviewListItemBinding.tvHeadline.setText(headline);

        // set summary.
        movieReviewListItemBinding.tvSummary.setText(review.getSummaryShort());
    }

    @Override
    public int getItemCount() {
        return mReviewList == null ? 0 : mReviewList.size();
    }

    /**
     * {@link android.support.v7.widget.RecyclerView.ViewHolder} for the {@link MovieReviewListAdaptor}
     */
    class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        public MovieReviewListItemBinding movieReviewListItemBinding;

        public MovieReviewViewHolder(MovieReviewListItemBinding binding) {
            super(binding.getRoot());
            movieReviewListItemBinding = binding;
        }
    }
}
