package com.example.quikrclone.adapters;

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
import com.example.quikrclone.CommentActivity;
import com.example.quikrclone.models.Post;
import com.example.quikrclone.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.RecycleHolder>{
    private Context context;
    private ArrayList<Post> mEa;
    private FirebaseFirestore firebaseFirestore;
    public class RecycleHolder extends RecyclerView.ViewHolder
    {
        private TextView title,desc,time,name,category;
        private ImageView imageView,comment;
        public RecycleHolder( View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            time = itemView.findViewById(R.id.time);
            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);
            imageView = itemView.findViewById(R.id.img);
            comment = itemView.findViewById(R.id.comment);
        }
    }
    public PostAdapter(ArrayList<Post> a)
    {
        mEa=a;
    }

    @NonNull
    @Override
    public PostAdapter.RecycleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle,parent,false);
        RecycleHolder recycleHolder = new RecycleHolder(view);
        context=view.getContext();
        return recycleHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull PostAdapter.RecycleHolder holder, int position) {
        Post b = mEa.get(position);

        long milis = b.getTime_stamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy HH:mm aa", new Date(milis)).toString();
        holder.title.setText(b.getTitle());
        holder.desc.setText(b.getDesc());
        holder.time.setText(dateString);
        holder.category.setText(b.getCategory());
        holder.name.setText(b.getUserName());

        final String blogPostId=b.BlogPostId;
        firebaseFirestore = FirebaseFirestore.getInstance();
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("id",blogPostId);
                context.startActivity(intent);
            }
        });
        Glide.with(context).load(b.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mEa.size();
    }

}

