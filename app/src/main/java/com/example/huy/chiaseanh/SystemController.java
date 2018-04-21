package com.example.huy.chiaseanh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

import static com.example.huy.chiaseanh.Constant.IMAGE_REQUEST_CODE;

public class SystemController {
    // mở  camera
    public static void openCamera(Context context){

        // khai báo intent camera:
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // mở intent camera với khóa (cờ nhận dạng) là 1 số bất kì để nhận biết activity camera nào trả về (có thể thay 1 bằng mã bất kì)
        // cái tương tự như void và return của hàm vậy, còn activity thay vào đó là startActivity và startActivityForResult;
        // sau khi chọn file sẽ trả về activity nguồn gọi ra activity camera
        // (ở đây là activity profile, xem hàm onActivityResult bên activity profile)
        ((Activity)context).startActivityForResult(intent,IMAGE_REQUEST_CODE); // Xem bên class Constant
    }

    // mở  thư viện ảnh
    public static void openGallery(Context context){

        // khai báo intent thư viện media:
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Lấy đường dẫn file được chọn
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path = directory.getPath();
        // chuyển dường dẫn dạng string thành Uri
        Uri data = Uri.parse(path);
        // lọc định dạng của thư viện media trên thiết bị chỉ hiển thị file dạng ảnh (png, jpg, jpeg...)
        intent.setDataAndType(data,"image/*");
        ((Activity)context).startActivityForResult(intent,IMAGE_REQUEST_CODE);
    }
}