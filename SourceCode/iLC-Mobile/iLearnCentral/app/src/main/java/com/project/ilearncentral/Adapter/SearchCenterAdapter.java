package com.project.ilearncentral.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.project.ilearncentral.Activity.Main;
import com.project.ilearncentral.Activity.ViewLearningCenter;
import com.project.ilearncentral.Activity.ViewUser;
import com.project.ilearncentral.Model.LearningCenter;
import com.project.ilearncentral.MyClass.Account;
import com.project.ilearncentral.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SearchCenterAdapter extends RecyclerView.Adapter<SearchCenterAdapter.SearchCenterHolder> {

    private Context context;
    private List<LearningCenter> centers;
    private int lastPosition = -1;

    public SearchCenterAdapter(Context context, List<LearningCenter> centers) {
        this.context = context;
        this.centers = centers;
    }

    @NonNull
    @Override
    public SearchCenterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recyclerview_search_user_row, parent, false);

        return new SearchCenterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchCenterHolder holder, int position) {
        final LearningCenter center = centers.get(position);

        holder.root.setAnimation(AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.move_up : R.anim.move_down));

        holder.type.setText(center.getServiceType() + "\n" + center.getDescription());
        holder.name.setText(center.getBusinessName());
//        if (!center.getLogo().isEmpty())
        if (holder.image.getBackground() == null)
//            Picasso.get().load(Uri.parse(center.getLogo())).error(R.drawable.user).fit().into(holder.image);
            Glide.with(context).load(center.getLogo()).error(R.drawable.logo_icon).fitCenter().into(holder.image);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewLearningCenter.class);
                Account.addData("openLC", center.getCenterId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return centers.size();
    }

    public class SearchCenterHolder extends RecyclerView.ViewHolder {

        private CardView root;
        private RelativeLayout parent;
        private TextView name, type;
        private ImageView image;

        SearchCenterHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.search_user_root);
            parent = itemView.findViewById(R.id.search_user_parent);
            name = itemView.findViewById(R.id.search_username);
            type = itemView.findViewById(R.id.search_user_type);
            image = itemView.findViewById(R.id.search_user_image);
        }
    }
}
