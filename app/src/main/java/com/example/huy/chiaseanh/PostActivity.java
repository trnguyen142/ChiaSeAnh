package com.example.huy.chiaseanh;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.huy.chiaseanh.Constant.IMAGE_REQUEST_CODE;

public class PostActivity extends AppCompatActivity implements Serializable, LocationListener {

    private TextView txvName, txvLocation;
    private String uID;
    private MyUser user = new MyUser();
    private ImageView btnGui;
    private ImageView btnBack;
    private ImageView btnImg;
    private EditText edtPost;
    private String keyNew;
    private Geocoder geocoder;
    private String locationArr[];
    LocationManager mLocationManager;
    private double longitude;
    private double latitude;
    private PhotoDialog customDialog;
    private Uri imageUri;
    private ImageView imgPost;
    private ImageView imgAvatar;
    private int checkSelectImage = -1;
    private AlertDialog.Builder builderAvatar;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        user = (MyUser) getIntent().getSerializableExtra("user");
        uID = getIntent().getExtras().getString("Uid");
        txvName = (TextView) findViewById(R.id.txtName);
        btnGui = (ImageView) findViewById(R.id.btnGui);
        btnImg = (ImageView) findViewById(R.id.btnImg);
        edtPost = (EditText) findViewById(R.id.edtNoiDung);
        imgPost=(ImageView)findViewById(R.id.imgPost);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        txvName.setText(user.getName());

        txvLocation = (TextView) findViewById(R.id.txtLocationPost);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        locationArr = new String[10];
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PostActivity.this, MainActivity.class);
                i.putExtra("Uid", uID);
                PostActivity.this.startActivity(i);
            }
        });
        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postNews();


            }
        });
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSelectImage = 0;
                openCustomDialog();
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builderAvatar = new AlertDialog.Builder(PostActivity.this);
                builderAvatar.setTitle("Thông báo").setMessage("Bạn có muốn thay đổi ảnh đại diện?")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkSelectImage = 1;
                                openCustomDialog();



                            }
                        })
                        .setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog dialog = builderAvatar.create();
                dialog.show();
            }
        });
        //XIn quyền. Gọi hàm ở dưới lên đây

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Cái IF này nó tự sinh
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if(location != null)
        {
            //Lấy tọa độ
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.d("latlong", latitude + "/" + longitude);

            // bỏ vô lấy ra địa chỉ
            List<Address> addresses = null;
            try {
                geocoder = new Geocoder(PostActivity.this, Locale.getDefault());
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int maxAddressLine = addresses.get(0).getMaxAddressLineIndex();


            String cityName = addresses.get(0).getAddressLine(maxAddressLine);
            locationArr = cityName.split(", ");
            txvLocation.setText(locationArr[locationArr.length - 4] + ", " + locationArr[locationArr.length - 3] + ", " + locationArr[locationArr.length - 2] + ", " + locationArr[locationArr.length - 1]);
        }
//        LocationListener locationListner = new PostActivity() {
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onLocationChanged(Location location) {
//                // TODO Auto-generated method stub
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//                List<Address> addresses = null;
//                try {
//                    geocoder = new Geocoder(PostActivity.this, Locale.getDefault());
//                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                int maxAddressLine = addresses.get(0).getMaxAddressLineIndex();
//
//
//                String cityName = addresses.get(0).getAddressLine(maxAddressLine);
//                locationArr = cityName.split(", ");
//                Log.d("MRG","It worked");
//                txvLocation.setText(locationArr[locationArr.length - 4] + ", " + locationArr[locationArr.length - 3] + ", " + locationArr[locationArr.length - 2] + ", " + locationArr[locationArr.length - 1]);
//            }
//        };
//        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListner);
    loadAvatar();
    }
    public void loadAvatar()
    {
        databaseReference.child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(uID))
                {
                    if(dataSnapshot.child("url").exists())
                    {
                        Picasso.with(PostActivity.this).load(dataSnapshot.child("url").getValue().toString()).into(imgAvatar);

                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(uID))
                {
                    Picasso.with(PostActivity.this).load(dataSnapshot.child("url").getValue().toString()).into(imgAvatar);
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

    // Khỏi tạo và mở dialog tùy chỉnh
    private void openCustomDialog() {
        customDialog = new PhotoDialog(PostActivity.this);
        customDialog.setTitle("Chọn ảnh của bạn");
        customDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Nếu nhận <ActivityResult> từ activity CHỌN ẢNH (không phân biệt từ thư viện hay chụp ảnh mới)
        // và kiểm tra có chọn ảnh hay không để xử lý hàm if này
        if (requestCode == IMAGE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            imageUri = data.getData();
            // Lấy được tấm ảnh rồi thì mở activity cắt ảnh
            CropImage.activity(imageUri)// thảy cái uri vào intent crop image
                    .setAspectRatio(1, 1)// set tỉ lệ cắt (ở đây cắt avatar nên để tỉ lệ 1:1 cho nó thành hình vuông)
                    .setMaxCropResultSize(1920,1080) // kích thước cắt tối đa: 500x500 pixel
                    .setMinCropWindowSize(1000, 1000) // kích thước cắt tối thiểu: 50x50 pixel
                    .setBackgroundColor(R.color.colorAccent) // tự hiểu
                    .start(this); // tự hiểu
        }
        // Nếu nhận <ActivityResult> từ activity CẮT ẢNH
        // và nhận được 1 tấm ảnh đã cắt thì xử lý hàm if này:
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // Lấy đường dẫn
                imageUri = result.getUri();
                // Đóng dialog mở lên lúc nãy
                customDialog.dismiss();
                // upload image lên firebase
                if(checkSelectImage == 1)
                {
                    Calendar c = Calendar.getInstance();
                    String s = c.getTimeInMillis() + "";
                    FirebaseStorage.getInstance().getReference().child(s).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String url = taskSnapshot.getDownloadUrl().toString();
                            databaseReference.child("user").child(uID).child("url").setValue(url);
                        }
                    });
                    Picasso.with(PostActivity.this).load(imageUri).into(imgAvatar);
                }
                if(checkSelectImage == 0)
                {
                    Picasso.with(PostActivity.this).load(imageUri).into(imgPost);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // nếu xảy ra lỗi thì cũng đóng dialog luôn:
                customDialog.dismiss();
            }
        }


    }
    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void postNews()
    {

        String title = edtPost.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String time = sdf.format(System.currentTimeMillis());
        View forcus = null;
        if(TextUtils.isEmpty(title))
        {
            edtPost.setError("Nội dung trống");
            forcus = edtPost;
            forcus.requestFocus();
            return;
        }
        keyNew = databaseReference.child("news").child(uID).push().getKey();
        databaseReference.child("news").child(uID).child(keyNew).child("content").setValue(title);
        databaseReference.child("news").child(uID).child(keyNew).child("time").setValue(time);
        databaseReference.child("news").child(uID).child(keyNew).child("name").setValue(user.getName());
        databaseReference.child("news").child(uID).child(keyNew).child("lat").setValue(latitude);
        databaseReference.child("news").child(uID).child(keyNew).child("lng").setValue(longitude);
        Calendar c = Calendar.getInstance();
        String s = c.getTimeInMillis() + "";
        if(imageUri !=  null)
        {
            FirebaseStorage.getInstance().getReference().child(s).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getDownloadUrl().toString();
                    databaseReference.child("news").child(uID).child(keyNew).child("url").setValue(url);
                }
            });
        }

        Toast.makeText(PostActivity.this, "Đăng thành công", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(PostActivity.this, MainActivity.class);
        i.putExtra("Uid", uID);
        PostActivity.this.startActivity(i);
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        List<Address> addresses = null;
        try {
            geocoder = new Geocoder(PostActivity.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int maxAddressLine = addresses.get(0).getMaxAddressLineIndex();


        String cityName = addresses.get(0).getAddressLine(maxAddressLine);
        locationArr = cityName.split(", ");
        txvLocation.setText(locationArr[locationArr.length - 4] + ", " + locationArr[locationArr.length - 3] + ", " + locationArr[locationArr.length - 2] + ", " + locationArr[locationArr.length - 1]);
        System.out.println("onLocationChanged.." + latitude);
        System.out.println("onLocationChanged.." + longitude);
    }

    //Xin quyền
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
