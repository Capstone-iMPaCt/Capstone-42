package com.project.ilearncentral.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.project.ilearncentral.Activity.Home;
import com.project.ilearncentral.Model.Post;
import com.project.ilearncentral.R;

import java.util.List;

public class PostFeedAdapter extends RecyclerView.Adapter<PostFeedAdapter.PostViewHolder> {

    private Context context;
    private List<Post> posts;
    private Intent intent;

    public PostFeedAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new PostViewHolder(view);
    }

    // On bind/display animation
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, final int position) {
        holder.containerLayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));
        holder.headerLayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

        holder.userImageView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        getImage(holder.userImageView, posts.get(position).getNewsUserImageView());
        holder.contentImageView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        getImage(holder.contentImageView, posts.get(position).getNewsContentImageView());

        holder.titleTextView.setText(posts.get(position).getTitleTextView());
        holder.dateTextView.setText(posts.get(position).getDateTextView());
        holder.timeTextView.setText(posts.get(position).getTimeTextView());
        holder.contentTextView.setText(posts.get(position).getContentTextView());

        holder.userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, Home.class);
                intent.putExtra("title", posts.get(position).getTitleTextView());
                context.startActivity(intent);
            }
        });

//        holder.userImageView.setOnClickListener(this);
//        holder.titleTextView.setOnClickListener(this);
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    /*@Override
    public void onClick(View v, int position) {
        switch (v.getId()) {
            case R.id.user_imageview:
                break;
            case R.id.post_title_textview:
                intent = new Intent(context, Home.class);
                intent.putExtra("title", posts.get(position).getTitleTextView());
                context.startActivity(intent);
                break;
            default:
                break;
        }
    }*/

    private void getImage(ImageView newsUserImageView, int newsContentImageView) {
        Glide
                .with(context)
                .load(newsContentImageView)
                .fitCenter()
                .apply(new RequestOptions())
                .into(newsUserImageView);
    }

    private void getImage(ImageView newsUserImageView, int newsContentImageView, int height) {
        Glide
                .with(context)
                .load(newsContentImageView)
                .fitCenter()
                .apply(new RequestOptions().override(height))
                .into(newsUserImageView);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout containerLayout, headerLayout;
        private ImageView userImageView, contentImageView;
        private TextView titleTextView, dateTextView, timeTextView, contentTextView;

        PostViewHolder(View itemView) {
            super(itemView);
            containerLayout = (RelativeLayout) itemView.findViewById(R.id.container_layout);
            userImageView = (ImageView) itemView.findViewById(R.id.user_imageview);
            titleTextView = (TextView) itemView.findViewById(R.id.post_title_textview);
            headerLayout = (RelativeLayout) itemView.findViewById(R.id.timestamp_layout);
            dateTextView = (TextView) itemView.findViewById(R.id.date_textview);
            timeTextView = (TextView) itemView.findViewById(R.id.time_textview);
            contentImageView = (ImageView) itemView.findViewById(R.id.content_imageview);
            contentTextView = (TextView) itemView.findViewById(R.id.content_textview);
        }
    }
}

