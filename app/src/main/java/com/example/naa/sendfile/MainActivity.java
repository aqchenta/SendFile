package com.example.naa.sendfile;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.provider.MediaStore;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
import android.util.Log;
import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener /*  implementing click listener */ {
    //a constant to track the file chooser intent
//    Adapter_VideoFolder obj_adapter;
//    ArrayList<Model_Video> al_video = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 234;

    //Buttons
    private Button buttonChoose;
    private Button buttonUpload;

    //ImageView
    private ImageView imageView;
    private VideoView videoView;
    public StorageReference mStorageRef;

    //a Uri object to store file path
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseStorage storage;
        StorageReference storageRef;

        mStorageRef = FirebaseStorage.getInstance().getReference();

        //getting views from layout
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);

        imageView = (ImageView) findViewById(R.id.imageView);

        //attaching listener
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
//        init();
    }
//    private void init(){
//
//        fn_checkpermission();
//
//    }

//    private void fn_checkpermission(){
//        /*RUN TIME PERMISSIONS*/
//
//        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
//                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
//
//            } else {
//                ActivityCompat.requestPermissions(MainActivity.this,
//                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
//                        REQUEST_PERMISSIONS);
//            }
//        }else {
//            Log.e("Else","Else");
//            fn_video();
//        }
//    }

//    public void fn_video() {
//
//        int int_position = 0;
//        Uri uri;
//        Cursor cursor;
//        int column_index_data, column_index_folder_name,column_id,thum;
//
//        String absolutePathOfImage = null;
//        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//
//        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,MediaStore.Video.Media._ID,MediaStore.Video.Thumbnails.DATA};
//
//        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
//        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
//
//        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
//        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
//        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
//
//        while (cursor.moveToNext()) {
//            absolutePathOfImage = cursor.getString(column_index_data);
//            Log.e("Column", absolutePathOfImage);
//            Log.e("Folder", cursor.getString(column_index_folder_name));
//            Log.e("column_id", cursor.getString(column_id));
//            Log.e("thum", cursor.getString(thum));
//
//            Model_Video obj_model = new Model_Video();
//            obj_model.setBoolean_selected(false);
//            obj_model.setStr_path(absolutePathOfImage);
//            obj_model.setStr_thumb(cursor.getString(thum));
//
//            al_video.add(obj_model);
//
//        }
//
//
//        obj_adapter = new Adapter_VideoFolder(getApplicationContext(),al_video,MainActivity.this);
//        videoView.setAdapter(obj_adapter);
//
//    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {

//                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,MediaStore.Video.Media._ID,MediaStore.Video.Thumbnails.DATA};
//                final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        //if the clicked button is choose
        if (view == buttonChoose) {
//            fn_checkpermission();
                showFileChooser();
        }
        //if the clicked button is upload
        else if (view == buttonUpload) {
            uploadFile();

        }
    }
    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference riversRef = mStorageRef.child("images/pic.jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        switch (requestCode) {
//            case REQUEST_PERMISSIONS: {
//                for (int i = 0; i < grantResults.length; i++) {
//                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        fn_video();
//                    } else {
//                        Toast.makeText(MainActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        }
//    }

}