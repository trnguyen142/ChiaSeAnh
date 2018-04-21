package com.example.huy.chiaseanh;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huy.chiaseanh.RecyclerViewAdapter.RecyclerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;


public class MainActivity extends TabActivity implements Serializable
{

    private RecyclerView mRecyclerViewPerson, mRecyclerViewNews;
    private RecyclerViewAdapter mRcvAdapterPerson;
    private RecyclerViewAdapter mRcvAdapterNews;
    private List<MyData> dataPerson, dataNews;
    private List<String>  arrUIDNews, arrKeysPerson, arrKeysNews;
    private List<List<String>> arrKeyNews = new ArrayList<List<String>>();
    private String uID;
    private MyUser user = new MyUser();
    private FloatingActionButton btnPost;
    private Geocoder geocoder;
    private String locationArr[];
    private int postionPerson = -1, postionNews = -1;
    private ImageView btnLogOut;
    private TextView txvNews;
    private List<LatLong> arrLatLong = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uID = getIntent().getExtras().getString("Uid");
        btnPost = (FloatingActionButton) findViewById(R.id.btnPost);
        btnLogOut = (ImageView) findViewById(R.id.btnLogOut);
        txvNews = (TextView) findViewById(R.id.txvNews);
        txvNews.setText("Bảng tin cá nhân");
        locationArr = new String[10];
        mRecyclerViewPerson = (RecyclerView) findViewById(R.id.recyclePerson);
        mRecyclerViewNews = (RecyclerView) findViewById(R.id.recycleNews);

        dataPerson = new ArrayList<>();
        dataNews = new ArrayList<>();
        arrUIDNews = new ArrayList<>();
        arrKeysPerson = new ArrayList<>();
        arrKeysNews = new ArrayList<>();

        mRcvAdapterPerson = new RecyclerViewAdapter(MainActivity.this,dataPerson);
        mRcvAdapterNews = new RecyclerViewAdapter(MainActivity.this,dataNews);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewPerson.setLayoutManager(layoutManager);
        mRecyclerViewPerson.setAdapter(mRcvAdapterPerson);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewNews.setLayoutManager(layoutManager1);
        mRecyclerViewNews.setAdapter(mRcvAdapterNews);

        //Tab như trong bài Lab
        final TabHost tabHost = getTabHost();
        TabHost.TabSpec spec  = tabHost.newTabSpec("tag1");
        spec.setContent(R.id.recyclePerson);
        spec.setIndicator("",getResources().getDrawable(R.drawable.person));
        getTabHost().addTab(spec);
        spec = getTabHost().newTabSpec("tag2");
        spec.setContent(R.id.recycleNews);
        spec.setIndicator("", getResources().getDrawable(R.drawable.news));
        getTabHost().addTab(spec);
        getTabHost().setCurrentTab(0);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                switch (tabHost.getCurrentTab())
                {
                    case 0:
                        txvNews.setText("Bảng tin cá nhân");
                        break;
                    case 1:
                        txvNews.setText("Bảng tin cộng đồng");
                        break;
                }
            }
        });
        //Nút đăng xuất. Chuyễn sang Login Activity
        btnLogOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Thông báo")
                        .setMessage("Bạn có muốn đăng xuất")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                MainActivity.this.startActivity(i);
                            }
                        })
                        .setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        //Nút đăng bài(nút dấu cộng bên gốc phải) chuyển sang Post Activity
        btnPost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(MainActivity.this, PostActivity.class);
                i.putExtra("Uid", uID);
                i.putExtra("user", user);
                MainActivity.this.startActivity(i);
            }
        });
        //Thiết lập click các Item trong danh sách bên Tab bài đăng cá nhân(click trả về vị trí). SetOnClickListener bên Adapter bằng interface
        mRcvAdapterPerson.setOnItemClickedListener(new RecyclerViewAdapter.OnItemClickedListener()
        {
            @Override
            public void onItemClick(final int p)
            {
                //Khi click vào bài đăng thì lấy ra tọa độ (Lat, Long) và chuyển dữ liệu sang Maps Activity để marker vào tọa độ
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Thông báo").setMessage("Bạn có muốn di chuyện tới địa điểm này?")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i =new Intent(MainActivity.this, MapsActivity.class);
                                i.putExtra("location", arrLatLong.get(p));
                                i.putExtra("Uid", uID);
                                MainActivity.this.startActivity(i);
                            }
                        })
                        .setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //Thiết lập click các Item trong danh sách bên Tab bài đăng cộng đồng(click trả về vị trí). SetOnClickListener bên Adapter bằng interface
        mRcvAdapterNews.setOnItemClickedListener(new RecyclerViewAdapter.OnItemClickedListener()
        {
            @Override
            public void onItemClick(final int p)
            {
                //Khi click vào bài đăng thì lấy ra tọa độ (Lat, Long) và chuyển dữ liệu sang Maps Activity để marker vào tọa độ
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Thông báo").setMessage("Bạn có muốn di chuyện tới địa điểm này?")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i =new Intent(MainActivity.this, MapsActivity.class);
                                i.putExtra("location", arrLatLong.get(p));
                                i.putExtra("Uid", uID);
                                MainActivity.this.startActivity(i);
                            }
                        })
                        .setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });
        //Thiết lập sự kiện nhấn nút "Thích" và "Bình luận"
        mRcvAdapterPerson.setMyClickLikeComment(new RecyclerViewAdapter.MyClickLikeComment()
        {
            @Override
            public void onLike(int p)
            {
                //Điồng bộ khi nhấn nút "Thích" bên Tab cá nhân và Tab cộng đồng
                boolean check = false;
                Toast.makeText(MainActivity.this, "Like Person" + p, Toast.LENGTH_SHORT).show();
                postionPerson = p;
                for(int i = 0; i < arrKeysNews.size(); i++)
                {
                    if(arrKeysPerson.get(postionPerson).equals(arrKeysNews.get(i)))
                    {
                        postionNews = i;
                    }
                }
                //Đăng nhánh Like vào bài viết
                String btnStr[] = new String[3];
                btnStr = dataPerson.get(p).getStatus().split(" ");
                if(btnStr[1].equals("thích"))
                {
                    check = true;
                    uploadDisLike(0, arrKeysPerson.get(p));
                }
                Log.d("getStatus", check + "");
                if((dataPerson.get(p).getStatus()).compareTo("Thích") != -1 && !check)
                {
                    uploadLike(0,arrKeysPerson.get(p));
                }
            }
            @Override
            public void onComment(int p)
            {
                postionPerson = p;
                for(int i = 0; i < arrKeysNews.size(); i++)
                {
                    if(arrKeysPerson.get(postionPerson).equals(arrKeysNews.get(i)))
                    {
                        postionNews = i;
                    }
                }

                CustomDialogClass dialog = new CustomDialogClass(MainActivity.this, uID, uID, arrKeysPerson.get(p));
                dialog.show();
            }
        });
        mRcvAdapterNews.setMyClickLikeComment(new RecyclerViewAdapter.MyClickLikeComment()
        {
            @Override
            public void onLike(int p)
            {
                boolean check = false;
                Toast.makeText(MainActivity.this, "Like News", Toast.LENGTH_SHORT).show();
                postionNews = p;
                for(int i = 0; i < arrKeysPerson.size(); i++)
                {
                    if(arrKeysNews.get(postionNews).equals(arrKeysPerson.get(i)))
                    {
                        postionPerson = i;
                    }
                }
                Log.d("getStatus", dataNews.get(p).getStatus());
                Log.d("getStatus", (dataNews.get(p).getStatus()).compareTo("Thích") + "");

                String btnStr[] = new String[3];
                btnStr = dataNews.get(p).getStatus().split(" ");
                if(btnStr[1].equals("thích"))
                {
                    check = true;
                    uploadDisLike(1, arrKeysNews.get(p));
                }
                Log.d("getStatus", check + "");
                if((dataNews.get(p).getStatus()).compareTo("Thích") != -1 && !check)
                {
                    uploadLike(1,arrKeysNews.get(p));
                }
            }
            @Override
            public void onComment(int p)
            {
                postionNews = p;
                for(int i = 0; i < arrKeysPerson.size(); i++)
                {
                    if(arrKeysNews.get(postionNews).equals(arrKeysPerson.get(i)))
                    {
                        postionPerson = i;
                    }
                }
                CustomDialogClass dialog = new CustomDialogClass(MainActivity.this, uID, arrUIDNews.get(p), arrKeysNews.get(p));
                dialog.show();
            }
        });
        loadPerson();
        loadNews();
        getInfo();

    }
    //check = 0 là Cá nhân, =1 là cộng động
    public void uploadDisLike(int check, String keyNews)
    {
        switch (check)
        {
            case 0:
                databaseReference.child("news").child(uID).child(keyNews).child("like").child(uID).removeValue();
                break;
            case 1:
                Log.d("arrUIDNews", arrUIDNews.size() + "");
                for(int i = 0; i < arrKeyNews.size(); i++)
                {
                    for(int j = 0; j < arrKeyNews.get(i).size(); j++)
                    {
                        if(arrKeyNews.get(i).get(j).equals(keyNews))
                        {
                            databaseReference.child("news").child(arrUIDNews.get(i)).child(keyNews).child("like").child(uID).removeValue();
                            return;
                        }
                    }
                }
                break;
        }
    }
    public void uploadLike(int check, String keyNews)
    {
        switch (check)
        {
            case 0:
                databaseReference.child("news").child(uID).child(keyNews).child("like").child(uID).setValue(0);
                break;
            case 1:
                Log.d("arrUIDNews", arrUIDNews.size() + "");
                for(int i = 0; i < arrKeyNews.size(); i++)
                {
                    for(int j = 0; j < arrKeyNews.get(i).size(); j++)
                    {
                        if(arrKeyNews.get(i).get(j).equals(keyNews))
                        {
                            databaseReference.child("news").child(arrUIDNews.get(i)).child(keyNews).child("like").child(uID).setValue(0);
                            return;
                        }
                    }
                }
                break;
        }
    }
    public void loadNews()
    {
        databaseReference.child("news").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot1, String s) {


                databaseReference.child("news").child(dataSnapshot1.getKey()).addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(final DataSnapshot dataSnapshot2, String s)
                    {
                        arrKeyNews.add(new ArrayList<String>());
                        arrUIDNews.add(dataSnapshot1.getKey().toString());
                        arrKeysNews.add(dataSnapshot2.getKey().toString());
                        arrKeyNews.get(arrKeyNews.size() - 1).add(dataSnapshot2.getKey().toString());
                        Log.d("likeStr", arrKeysNews.size() + "");
                        final MyData[] item = {new MyData()};
                        LatLong latlong = new LatLong();
                        item[0] = dataSnapshot2.getValue(MyData.class);
                        databaseReference.child("user").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot3, String s) {
                                if(dataSnapshot3.getKey().equals(dataSnapshot1.getKey()))
                                {
                                    if(dataSnapshot3.child("url").exists())
                                    {
                                        item[0].setUrlAvatar(dataSnapshot3.child("url").getValue().toString());

                                    }
                                }

                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot3, String s) {
                                if(dataSnapshot3.getKey().equals(dataSnapshot1.getKey()))
                                {
                                    if(dataSnapshot3.child("url").exists())
                                    {
                                        item[0].setUrlAvatar(dataSnapshot3.child("url").getValue().toString());

                                    }
                                }
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
                        latlong = dataSnapshot2.getValue(LatLong.class);
                        double lat = latlong.getLat();
                        double lng = latlong.getLng();
                        arrLatLong.add(latlong);

                        if (lat != 0 && lng != 0)
                        {
                            List<Address> addresses = null;
                            try
                            {
                                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                addresses = geocoder.getFromLocation(lat, lng, 1);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                            int maxAddressLine = addresses.get(0).getMaxAddressLineIndex();
                            String cityName = addresses.get(0).getAddressLine(maxAddressLine);
                            locationArr = cityName.split(", ");

                            item[0].setLocation(locationArr[locationArr.length - 3] + ", " + locationArr[locationArr.length - 2] + ", " + locationArr[locationArr.length - 1]);

                        }
                    if(dataSnapshot2.child("like").exists())
                    {
                        List<String> arrTempLike = new ArrayList<>();
                        String likeStr = dataSnapshot2.child("like").getValue().toString();
                        Log.d("likeStr", likeStr);
                        String temp = "";
                        boolean checkLike = false;
                        for(int i = 1; i  < likeStr.length() - 1; i++)
                        {
                            if(likeStr.charAt(i) == '=' && likeStr.charAt(i + 1) == '0')
                            {
                                Log.d("temp", temp);
                                arrTempLike.add(temp);
                                Log.d("size", arrTempLike.size() + "");
                                if(arrTempLike.get(arrTempLike.size() - 1).equals(uID))
                                {
                                    checkLike = true;
                                }
                                if(checkLike)
                                {
                                    item[0].setStatus("Đã thích (" + arrTempLike.size() + ")");
                                }
                                temp = "";
                                i += 3;
                                continue;
                            }
                            temp += likeStr.charAt(i);
                        }
                        if (!checkLike)
                         {
                            item[0].setStatus("Thích (" + arrTempLike.size() + ")");
                        }
                    }
                    else
                    {
                        item[0].setStatus("Thích (0)");
                    }
                    if (dataSnapshot2.child("comment").exists())
                    {
                        item[0].setCountComment("Bình luận (" + dataSnapshot2.child("comment").getChildrenCount() + ")");
                    }
                    else
                    {
                       item[0].setCountComment("Bình luận (0)");
                    }
                        dataNews.add(item[0]);
                        mRcvAdapterNews.notifyDataSetChanged();
                        mRecyclerViewNews.setAdapter(mRcvAdapterNews);
                        mRecyclerViewNews.invalidate();
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {
                        if(postionNews == -1)
                        {
                            postionNews = dataNews.size() - 1;
                        }
                        final MyData[] item = {new MyData()};
                        LatLong latlong = new LatLong();
                        item[0] = dataSnapshot.getValue(MyData.class);
                        dataNews.get(postionNews).setTime(item[0].getTime());
                        dataNews.get(postionNews).setName(item[0].getName());
                        dataNews.get(postionNews).setContent(item[0].getContent());
                        dataNews.get(postionNews).setUrl(item[0].getUrl());
                        latlong = dataSnapshot.getValue(LatLong.class);
                        double lat = latlong.getLat();
                        double lng = latlong.getLng();

                        if (lat != 0 && lng != 0)
                        {
                            List<Address> addresses = null;
                            try {
                                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                addresses = geocoder.getFromLocation(lat, lng, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            int maxAddressLine = addresses.get(0).getMaxAddressLineIndex();
                            String cityName = addresses.get(0).getAddressLine(maxAddressLine);
                            locationArr = cityName.split(", ");

                            dataNews.get(postionNews).setLocation(locationArr[locationArr.length - 3] + ", " + locationArr[locationArr.length - 2] + ", " + locationArr[locationArr.length - 1]);

                        }
                        if(dataSnapshot.child("like").exists())
                        {
                            List<String> arrTempLike = new ArrayList<>();
                            String likeStr = dataSnapshot.child("like").getValue().toString();
                            Log.d("likeStr", likeStr);
                            String temp = "";
                            boolean checkLike = false;
                            for(int i = 1; i  < likeStr.length() - 1; i++)
                            {
                                if(likeStr.charAt(i) == '=' && likeStr.charAt(i + 1) == '0')
                                {
                                    Log.d("temp", temp);
                                    arrTempLike.add(temp);
                                    Log.d("size", arrTempLike.size() + "");
                                    if(arrTempLike.get(arrTempLike.size() - 1).equals(uID))
                                    {
                                        checkLike = true;
                                    }
                                    if(checkLike)
                                    {
                                        dataNews.get(postionNews).setStatus("Đã thích (" + arrTempLike.size() + ")");
                                    }
                                    temp = "";
                                    i += 3;
                                    continue;
                                }
                                temp += likeStr.charAt(i);
                            }
                            if (!checkLike)
                            {
                                dataNews.get(postionNews).setStatus("Thích (" + arrTempLike.size() + ")");
                            }
                        }
                        else
                        {
                            dataNews.get(postionNews).setStatus("Thích (0)");
                        }
                        if (dataSnapshot.child("comment").exists())
                        {
                            dataNews.get(postionNews).setCountComment("Bình luận (" + dataSnapshot.child("comment").getChildrenCount() + ")");
                        }
                        else
                        {
                            dataNews.get(postionNews).setCountComment("Bình luận (0)");
                        }
                        mRcvAdapterNews.notifyDataSetChanged();
                        mRecyclerViewNews.invalidate();
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

    public void loadPerson() {
        databaseReference.child("news").child(uID).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                arrKeysPerson.add(dataSnapshot.getKey().toString());

                final MyData[] item = {new MyData()};
                LatLong latlong = new LatLong();
                item[0] = dataSnapshot.getValue(MyData.class);
                databaseReference.child("user").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot2, String s) {
                        if(dataSnapshot2.getKey().equals(uID))
                        {
                            if(dataSnapshot2.child("url").exists())
                            {
                                item[0].setUrlAvatar(dataSnapshot2.child("url").getValue().toString());
                            }
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot2, String s) {
                        if(dataSnapshot2.getKey().equals(uID))
                        {
                            if(dataSnapshot2.child("url").exists())
                            {
                                item[0].setUrlAvatar(dataSnapshot2.child("url").getValue().toString());

                            }
                        }
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
                latlong = dataSnapshot.getValue(LatLong.class);
                double lat = latlong.getLat();
                double lng = latlong.getLng();
                arrLatLong.add(latlong);
                if (lat != 0 && lng != 0)
                {
                    List<Address> addresses = null;
                    try
                    {
                        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int maxAddressLine = addresses.get(0).getMaxAddressLineIndex();
                    String cityName = addresses.get(0).getAddressLine(maxAddressLine);
                    locationArr = cityName.split(", ");
                    item[0].setLocation(locationArr[locationArr.length - 3] + ", " + locationArr[locationArr.length - 2] + ", " + locationArr[locationArr.length - 1]);
                }
                if (dataSnapshot.child("like").exists())
                {
                    List<String> arrTempLike = new ArrayList<>();
                    String likeStr = dataSnapshot.child("like").getValue().toString();
                    Log.d("likeStr", likeStr);
                    String temp = "";
                    boolean checkLike = false;
                    for (int i = 1; i < likeStr.length() - 1; i++) {
                        if (likeStr.charAt(i) == '=' && likeStr.charAt(i + 1) == '0')
                        {
                            Log.d("temp", temp);
                            arrTempLike.add(temp);
                            Log.d("size", arrTempLike.size() + "");
                            if (arrTempLike.get(arrTempLike.size() - 1).equals(uID))
                            {
                                checkLike = true;
                            }
                            if (checkLike)
                            {
                                item[0].setStatus("Đã thích (" + arrTempLike.size() + ")");
                            }
                            temp = "";
                            i += 3;
                            continue;
                        }
                        temp += likeStr.charAt(i);
                    }
                    if (!checkLike)
                    {
                        item[0].setStatus("Thích (" + arrTempLike.size() + ")");
                    }
                }
                else
                {
                    item[0].setStatus("Thích (0)");
                }
                if(dataSnapshot.child("comment").exists())
                {
                   item[0].setCountComment("Bình luận (" + dataSnapshot.child("comment").getChildrenCount() + ")");
                }
                else
                {
                   item[0].setCountComment("Bình luận (0)");
                }
                    dataPerson.add(item[0]);
                    mRcvAdapterPerson.notifyDataSetChanged();
                    mRecyclerViewPerson.setAdapter(mRcvAdapterPerson);
                    mRecyclerViewPerson.invalidate();

                    Log.d("arrKeysPerson", arrKeysPerson + "");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                if(postionPerson == -1)
                {
                    postionPerson = dataPerson.size() -1;
                }
                final MyData[] item = {new MyData()};
                LatLong latlong = new LatLong();
                item[0] = dataSnapshot.getValue(MyData.class);
                dataPerson.get(postionPerson).setName(item[0].getName());
                dataPerson.get(postionPerson).setContent(item[0].getContent());
                dataPerson.get(postionPerson).setTime(item[0].getTime());
                dataPerson.get(postionPerson).setUrl(item[0].getUrl());
                latlong = dataSnapshot.getValue(LatLong.class);
                double lat = latlong.getLat();
                double lng = latlong.getLng();
                if (lat != 0 && lng != 0)
                {
                    List<Address> addresses = null;
                    try
                    {
                        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(lat, lng, 1);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    int maxAddressLine = addresses.get(0).getMaxAddressLineIndex();
                    String cityName = addresses.get(0).getAddressLine(maxAddressLine);
                    locationArr = cityName.split(", ");
                    dataPerson.get(postionPerson).setLocation(locationArr[locationArr.length - 3] + ", " + locationArr[locationArr.length - 2] + ", " + locationArr[locationArr.length - 1]);
                }
                if (dataSnapshot.child("like").exists())
                {
                    List<String> arrTempLike = new ArrayList<>();
                    String likeStr = dataSnapshot.child("like").getValue().toString();
                    Log.d("likeStr", likeStr);
                    String temp = "";
                    boolean checkLike = false;
                    for (int i = 1; i < likeStr.length() - 1; i++) {
                        if (likeStr.charAt(i) == '=' && likeStr.charAt(i + 1) == '0')
                        {
                            Log.d("temp", temp);
                            arrTempLike.add(temp);
                            Log.d("size", arrTempLike.size() + "");
                            if (arrTempLike.get(arrTempLike.size() - 1).equals(uID))
                            {
                                checkLike = true;
                            }
                            if (checkLike)
                            {
                                dataPerson.get(postionPerson).setStatus("Đã thích (" + arrTempLike.size() + ")");
                            }
                            temp = "";
                            i += 3;
                            continue;
                        }
                        temp += likeStr.charAt(i);
                    }
                    if (!checkLike)
                    {
                        dataPerson.get(postionPerson).setStatus("Thích (" + arrTempLike.size() + ")");
                    }
                }
                else
                {
                    dataPerson.get(postionPerson).setStatus("Thích (0)");
                }
                if(dataSnapshot.child("comment").exists())
                {
                    dataPerson.get(postionPerson).setCountComment("Bình luận (" + dataSnapshot.child("comment").getChildrenCount() + ")");
                }
                else
                {
                    dataPerson.get(postionPerson).setCountComment("Bình luận (0)");
                }
                mRcvAdapterPerson.notifyDataSetChanged();
                mRecyclerViewPerson.invalidate();

                Log.d("arrKeysPerson", arrKeysPerson + "");
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

    public void getInfo()
    {
        databaseReference.child("user").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Log.d("user.getname", dataSnapshot + "");
                if (uID.equals(dataSnapshot.getKey().toString()))
                {
                    user = dataSnapshot.getValue(MyUser.class);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                if (uID.equals(dataSnapshot.getKey().toString()))
                {
                    user = dataSnapshot.getValue(MyUser.class);
                }
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
