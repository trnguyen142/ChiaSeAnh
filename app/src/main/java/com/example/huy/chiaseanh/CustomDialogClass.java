package com.example.huy.chiaseanh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Context c;
    public Dialog d;
    public ImageView btnDialog;
    private String keyUID, keyNews;
    private RecyclerView recycleCommentDialog;
    private RecyclerViewDialogAdapter adapterDialog;
    private List<Comment> arrDataDialog = new ArrayList<>();
    private String uID;
    private EditText edtComment;
    private String keyComment;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    public CustomDialogClass(Context a, String uID, String keyUID, String keyNews) {
        super(a);
        this.c = a;
        this.keyNews = keyNews;
        this.uID = uID;
        this.keyUID = keyUID;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        btnDialog = (ImageView) findViewById(R.id.btnDialog);
        recycleCommentDialog = (RecyclerView) findViewById(R.id.recycleCommentDialog);
        adapterDialog = new RecyclerViewDialogAdapter(c, arrDataDialog);
        edtComment = (EditText)findViewById(R.id.edtComment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleCommentDialog.setLayoutManager(layoutManager);
        recycleCommentDialog.setAdapter(adapterDialog);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        final String time = sdf.format(System.currentTimeMillis());
        loadCommentNews();
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = edtComment.getText().toString();
                View forcus = null;
                if(TextUtils.isEmpty(comment))
                {
                    edtComment.setError("Nội dung trống");
                    forcus = edtComment;
                    forcus.requestFocus();
                }
                else
                {
                    databaseReference.child("user").addChildEventListener(new ChildEventListener()
                    {
                        @Override
                        public void onChildAdded(final DataSnapshot dataSnapshot, String s)
                        {

                            if(dataSnapshot.getKey().equals(uID))
                            {
                                Log.d("UID CMT", uID);
                                Comment cmt = new Comment();
                                String email = dataSnapshot.child("email").getValue().toString();
                                Log.d("name", dataSnapshot.getValue().toString());
                                cmt.setName(dataSnapshot.child("name").getValue().toString());
                                if(dataSnapshot.child("url").exists())
                                {
                                    cmt.setUrl(dataSnapshot.child("url").getValue().toString());
                                }
                                cmt.setContent(edtComment.getText().toString());
                                cmt.setTime(time);

                                edtComment.setText("");
                                keyComment = databaseReference.child("news").child(keyUID).child(keyNews).child("comment").push().getKey();
                                databaseReference.child("news").child(keyUID).child(keyNews).child("comment").child(keyComment).setValue(cmt);
                                databaseReference.child("news").child(keyUID).child(keyNews).child("comment").child(keyComment).child("uid").setValue(uID);

                            }

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }


            }
        });


    }

    public void loadCommentNews()
    {
        databaseReference.child("news").child(keyUID).child(keyNews).child("comment").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s)
            {
                    final Comment[] cmt = {new Comment()};
                    cmt[0] = dataSnapshot.getValue(Comment.class);

                    arrDataDialog.add(cmt[0]);
                    adapterDialog.notifyDataSetChanged();
                    recycleCommentDialog.setAdapter(adapterDialog);
                    recycleCommentDialog.invalidate();
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {

                arrDataDialog.get(arrDataDialog.size() - 1).setName(dataSnapshot.child("name").getValue().toString());
                arrDataDialog.get(arrDataDialog.size() - 1).setTime(dataSnapshot.child("time").getValue().toString());
                arrDataDialog.get(arrDataDialog.size() - 1).setContent(dataSnapshot.child("content").getValue().toString());
                if(dataSnapshot.child("url").exists())
                {
                    arrDataDialog.get(arrDataDialog.size() - 1).setUrl(dataSnapshot.child("url").getValue().toString());
                }


                adapterDialog.notifyDataSetChanged();
                recycleCommentDialog.invalidate();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void onClick(View view) {

    }
}