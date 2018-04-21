package com.example.huy.chiaseanh;



import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

public class RecyclerViewDialogAdapter extends RecyclerView.Adapter<RecyclerViewDialogAdapter.RecyclerViewDialogHolder>{

    private List<Comment> data;
    private Context mContext;
    public RecyclerViewDialogAdapter(Context c, List<Comment> data) {
        this.data = data;
        this.mContext = c;
    }

    @Override
    public RecyclerViewDialogHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.item_dialog, parent, false);


        return new RecyclerViewDialogHolder(view);
    }



    @Override
    public void onBindViewHolder(final RecyclerViewDialogHolder holder, final int position) {
        holder.txvUserName.setText(data.get(position).getName());
        holder.txvTitle.setText(data.get(position).getContent());
        holder.txvTime.setText(data.get(position).getTime());
        Picasso.with(mContext).load(data.get(position).getUrl()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class RecyclerViewDialogHolder extends RecyclerView.ViewHolder{
        TextView txvUserName;
        TextView txvTitle;
        TextView txvTime;
        ImageView img;
        public RecyclerViewDialogHolder(View itemView) {
            super(itemView);
            txvUserName = (TextView) itemView.findViewById(R.id.txvNameDialog);
            txvTitle = (TextView) itemView.findViewById(R.id.txvTitleDialog);
            txvTime = (TextView) itemView.findViewById(R.id.txvTimeDialog);
            img = (ImageView) itemView.findViewById(R.id.imgAvatarDialog);

        }

    }



}
