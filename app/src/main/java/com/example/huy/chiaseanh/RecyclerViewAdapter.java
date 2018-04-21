package com.example.huy.chiaseanh;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.IMediaControllerCallback;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{

    private List<MyData> data;
    private Context mContext;
    public RecyclerViewAdapter(Context c, List<MyData> data) {
        this.data = data;
        this.mContext=c;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.item, parent, false);


        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        holder.txvName.setText(data.get(position).getName());
        holder.txvContent.setText(data.get(position).getContent());
        holder.txvTime.setText(data.get(position).getTime());
        holder.txvLocation.setText(data.get(position).getLocation());
        holder.btnLike.setText(data.get(position).getStatus());
        holder.btnComment.setText(data.get(position).getCountComment());
        Picasso.with(mContext).load(data.get(position).getUrl()).into(holder.imgNews);
        Picasso.with(mContext).load(data.get(position).getUrlAvatar()).into(holder.imgAvatar);
        holder.line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickedListener != null) {
                    onItemClickedListener.onItemClick(position);
                }
            }
        });
        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myClickLikeComment != null)
                {
                    myClickLikeComment.onLike(position);
                }
            }
        });
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myClickLikeComment != null)
                {
                    myClickLikeComment.onComment(position);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView txvName;
        TextView txvContent;
        TextView txvTime;
        TextView txvLocation;
        ImageView imgNews;
        ImageView imgAvatar;
        LinearLayout line;
        Button btnLike, btnComment;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            txvName = (TextView) itemView.findViewById(R.id.txvNameNews);
            txvContent = (TextView) itemView.findViewById(R.id.txvContentNews);
            txvTime = (TextView) itemView.findViewById(R.id.txvTimeNews);
            txvLocation = (TextView) itemView.findViewById(R.id.txvLocationNews);
            imgNews = (ImageView) itemView.findViewById(R.id.imgNews);
            imgAvatar = (ImageView) itemView.findViewById(R.id.imgAvatarNews);
            line = (LinearLayout) itemView.findViewById(R.id.line);
            btnLike = (Button) itemView.findViewById(R.id.btnLike);
            btnComment = (Button) itemView.findViewById(R.id.btnComment);
        }

    }

    public interface OnItemClickedListener {
        void onItemClick(int p);
    }
    public interface MyClickLikeComment{
        void onLike(int p);
        void onComment(int p);
    }
    private OnItemClickedListener onItemClickedListener;
    private MyClickLikeComment myClickLikeComment;
    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
    public void setMyClickLikeComment(MyClickLikeComment myClickLikeComment)
    {
        this.myClickLikeComment = myClickLikeComment;
    }

}
