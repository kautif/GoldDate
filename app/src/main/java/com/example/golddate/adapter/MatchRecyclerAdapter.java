package com.example.golddate.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.golddate.ChatActivity;
import com.example.golddate.R;
import com.example.golddate.util.Match;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MatchRecyclerAdapter extends RecyclerView.Adapter<MatchRecyclerAdapter.ViewHolder> {
    Context context;
    List<Match> mMatchList;
    public MatchRecyclerAdapter(Context context, List<Match> mMatchList) {
        this.context = context;
        this.mMatchList = mMatchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_match_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchRecyclerAdapter.ViewHolder holder, final int position) {
        holder.matchName.setText(mMatchList.get(position).getName());
        Glide.with(context).load(mMatchList.get(position).getImg_url()).into(holder.matchImage);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("doc_id", mMatchList.get(position).getUser_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMatchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView matchName;
        CircleImageView matchImage;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            matchName = itemView.findViewById(R.id.match_name_textView);
            matchImage = itemView.findViewById(R.id.match_imageView);
        }
    }
}
