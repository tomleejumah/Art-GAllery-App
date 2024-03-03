package com.leestream.artgallery.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.leestream.artgallery.Models.Posts;
import com.leestream.artgallery.Models.User;
import com.leestream.artgallery.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private Context context;
    private  List<Posts> mPosts;

    public ProfileAdapter(Context context, List<Posts> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mini_post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posts posts= mPosts.get(position);
        Picasso.get().load(posts.getImageUrl()).into(holder.imageView);

        ViewGroup.LayoutParams layoutParams = holder.myCARd.getLayoutParams();
        layoutParams.height = calculateImageViewHeight(context);
        holder.myCARd.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }


    private int calculateImageViewHeight(Context context) {
        // Calculate image height based on screen width or desired width
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        // Adjust as needed, e.g., divide by number of columns in the grid
        return screenWidth / 3; // assuming 3 columns
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private CardView myCARd;


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            myCARd = itemView.findViewById(R.id.myCard);
        }
    }

}
