package com.nishant.ecko.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nishant.ecko.R;
import com.nishant.ecko.data.network.model.FlickrSearchResponse;
import com.nishant.ecko.util.DrawableManager;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<FlickrSearchResponse.Photos.Photo> mPhotosList;
    private Context context;
    private DrawableManager drawableManager;

    static final int VIEW_IMAGE = 0;
    static final int VIEW_FOOTER = 1;

    public void setMorePagesAvailable(boolean morePagesAvailable) {
        this.morePagesAvailable = morePagesAvailable;
    }

    private boolean morePagesAvailable = true;

    public void setDrawableManager(DrawableManager drawableManager) {
        this.drawableManager = drawableManager;
    }

    ImageAdapter(ArrayList<FlickrSearchResponse.Photos.Photo> mPhotosList, Context context) {
        this.mPhotosList = mPhotosList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_FOOTER) {
            return new FooterViewHolder(LayoutInflater.from(context).inflate(R.layout.image_rv_footer, parent, false));
        }
        return new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_IMAGE) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            drawableManager.fetchDrawableOnThread("https://farm2.staticflickr.com/" + mPhotosList.get(position).getServer() + "/" + mPhotosList.get(position).getId() + "_" + mPhotosList.get(position).getSecret() + "_q.jpg"
                    , imageViewHolder.imageView
                    , imageViewHolder.progressBar
            );
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == mPhotosList.size()) {
            return VIEW_FOOTER;
        }
        return VIEW_IMAGE;
    }

    @Override
    public int getItemCount() {
        if (mPhotosList.size() > 0 && morePagesAvailable) {
            return mPhotosList.size() + 1;
        } else {
            return mPhotosList.size();
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ProgressBar progressBar;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rv_image_item);
            progressBar = itemView.findViewById(R.id.progress);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
