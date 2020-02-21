package com.project.ilearncentral.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.ilearncentral.Activity.AddEditFeed;
import com.project.ilearncentral.Model.Account;
import com.project.ilearncentral.Model.Post;
import com.project.ilearncentral.Model.Posts;
import com.project.ilearncentral.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostFeedAdapter extends RecyclerView.Adapter<PostFeedAdapter.PostViewHolder> {

    private Context context;
    private List<Post> posts;
    private Intent intent;
    private StorageReference storageRef;

    public PostFeedAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_post, parent, false);
        return new PostViewHolder(view);
    }

    private int lastPosition = -1;

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, final int position) {

        final Post post = posts.get(position);

//        holder.containerLayout.setAnimation(AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.move_up : R.anim.move_down));
//        holder.headerLayout.setAnimation(AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.move_up : R.anim.move_down));
//        holder.userImageView.setAnimation(AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.move_up : R.anim.move_down));
//        holder.contentImageView.setAnimation(AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.move_up : R.anim.move_down));

        getImage(holder.userImageView, "images/" , post.getPostSender());

        if (post.isWithImage())
            getImage(holder.contentImageView, "posts/" , post.getPostId());
        else
            holder.contentImageView.setVisibility(View.GONE);

        lastPosition = position;

        holder.titleTextView.setText(post.getPostTitle());
        holder.dateTextView.setText(post.getDatePosted());
        holder.timeTextView.setText(post.getTimePosted());
        holder.contentTextView.setText(post.getContent());

        holder.userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //view user
            }
        });
        if (!post.getPostSender().equals(Account.getStringData("username"))) {
            holder.editTextView.setVisibility(View.GONE);
        }

        holder.editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Posts.setCurPost(Posts.getIdOfPost(post.getPostSender(), post.getContent()));
                if (Posts.hasCurrent()) {
                    Intent i = new Intent(context, AddEditFeed.class);
                    i.putExtra("postId", post.getPostId());
                    context.startActivity(i);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void getImage(final ImageView imageView, String folderName, String filename) {
        storageRef.child(folderName).child(filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setVisibility(View.GONE);
            }
        });
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout containerLayout, headerLayout;
        private CircleImageView userImageView;
        private ImageView contentImageView;
        private TextView titleTextView, dateTextView, timeTextView, contentTextView, editTextView;

        PostViewHolder(View itemView) {
            super(itemView);

            containerLayout = itemView.findViewById(R.id.addon_container_relativelayout);
            userImageView = itemView.findViewById(R.id.user_imageview);
            titleTextView = itemView.findViewById(R.id.post_title_textview);
            headerLayout = itemView.findViewById(R.id.timestamp_layout);
            dateTextView = itemView.findViewById(R.id.date_textview);
            timeTextView = itemView.findViewById(R.id.time_textview);
            contentImageView = itemView.findViewById(R.id.content_imageview);
            contentTextView = itemView.findViewById(R.id.content_textview);
            editTextView = itemView.findViewById(R.id.edit_textview);

        }
    }
}

