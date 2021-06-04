package com.example.quikrclone;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.RecycleHolder>{
    private Context context;
    private ArrayList<Comment> mEa;
    private FirebaseFirestore firebaseFirestore;
    public class RecycleHolder extends RecyclerView.ViewHolder
    {
        private TextView name,comment;
        public RecycleHolder( View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            comment = itemView.findViewById(R.id.comment);
        }
    }
    public CommentAdapter(ArrayList<Comment> a)
    {
        mEa=a;
    }

    @NonNull
    @Override
    public CommentAdapter.RecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_list,parent,false);
        RecycleHolder recycleHolder = new RecycleHolder(view);
        context=view.getContext();
        return recycleHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.RecycleHolder holder, int position) {
        Comment b = mEa.get(position);

        holder.name.setText(b.getName());
        holder.comment.setText(b.getComment());
    }

    @Override
    public int getItemCount() {
        return mEa.size();
    }

}

