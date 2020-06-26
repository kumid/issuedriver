package com.ru.test.issuedriver.helpers.storage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ru.test.issuedriver.ui.registration.RegistrationViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

public class fbStorageUploads {
    private static final String TAG = "UploadActivity";
    private static final int RC_UPLOAD_STREAM = 101;
    private static final int RC_UPLOAD_FILE = 102;
    public static UploadTask mUploadTask;

    private  static Activity activity;

    private static StorageReference storageRef, folderRef, imageRef;
    public static void init(Activity _activity){
        activity = _activity;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public static void uploadFromFile(Context context, docItem item, String a_path) {

        uploadFromFilePath(a_path, item);
    }


    public static void uploadFromFilePath(String path, docItem item) {
        Log.e(TAG, "Strat - " + path);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());

        if(folderRef == null){
            String timeStampDtring = new SimpleDateFormat("yyyyMMdd").format(new Date());
            folderRef = storageRef.child(timeStampDtring);
        }

        String fbName = "JPEG_" + timeStamp;
        final Uri file = Uri.fromFile(new File(path));
        imageRef = folderRef.child(String.format("%s.jpg", fbName));

        mUploadTask = imageRef.putFile(file);

        Helper.initProgressDialog(activity);
        Helper.mProgressDialog.show();

        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Helper.dismissProgressDialog();
                Log.e(TAG, String.format("Failure: %s", exception.getMessage())); //mTextView.setText(String.format("Failure: %s", exception.getMessage()));
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Helper.dismissProgressDialog();
                //findViewById(R.id.button_upload_resume).setVisibility(View.GONE);
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e(TAG, uri.toString()); //mTextView.setText(uri.toString());
                        setPhotoFBpathInterface interf = ((setPhotoFBpathInterface)activity);
                        if(interf != null)
                            interf.setPath(uri.toString());
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                Helper.setProgress(progress);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //findViewById(R.id.button_upload_resume).setVisibility(View.VISIBLE);
                Log.e(TAG, "Пауза");//mTextView.setText(R.string.upload_paused);
            }
        });
    }
    public static void uploadFromFile(Context context, docItem item) {


        Uri contentUri = item.uri;
        String path = null;

        try {
            path = getFilePath(context, contentUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();

        }

//        if(path == null)  // костыль
//            path = "/storage/emulated/0/Android/data/com.ru.heydocdevelopment.heydoc/files/Pictures/" + contentUri.getLastPathSegment();

        if(path != null && path.length() != 0)
            uploadFromFilePath(path, item);
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {


            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public interface  setPhotoFBpathInterface {
        void setPath(String path);
    }

    /*






    public static void uploadFromStream() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, RC_UPLOAD_STREAM);
    }

    public static void uploadFromFile() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, RC_UPLOAD_FILE);
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(data == null)
                return;

            String path = Helper.getPath(activity, Uri.parse(data.getData().toString()));
            switch (requestCode) {
                case RC_UPLOAD_STREAM:
                    uploadFromStream(path);
                    break;
                case RC_UPLOAD_FILE:
                    uploadFromFilePath(path, null);
                    break;
            }
        }
    }

    private static void uploadFromStream(String path) {
        Helper.showDialog(activity);
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mUploadTask = imageRef.putStream(stream);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Helper.dismissDialog();
                Log.e(TAG, String.format("Failure: %s", exception.getMessage()));
                //mTextView.setText(String.format("Failure: %s", exception.getMessage()));
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Helper.dismissDialog();
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e(TAG, uri.toString()); //mTextView.setText(uri.toString());
                    }
                });
            }
        });
    }


    public static void uploadFromDataInMemory(ImageView mImageView, String picName) {
        imageRef = folderRef.child(String.format("%s.jpg", picName));
        Helper.showDialog(activity);
        // Get the data from an ImageView as bytes
        // Get the data from an ImageView as bytes
        //mImageView.setDrawingCacheEnabled(true);
        //mImageView.buildDrawingCache();
        //Bitmap bitmap = mImageView.getDrawingCache();
        Bitmap bitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int quality = 100;
        if(bitmap.getAllocationByteCount() > 5000000)
            quality = 80;
        if(bitmap.getAllocationByteCount() > 25000000)
            quality = 60;
        if(bitmap.getAllocationByteCount() > 50000000)
            quality = 40;

        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] data = baos.toByteArray();

        mUploadTask = imageRef.putBytes(data);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Helper.dismissDialog();
                Log.e(TAG, String.format("Failure: %s", exception.getMessage())); //mTextView.setText(String.format("Failure: %s", exception.getMessage()));
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Helper.dismissDialog();

                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e(TAG, uri.toString()); //mTextView.setText(uri.toString());
                    }
                });
            }
        });
    }








    public static void setUserID(String uid) {
        folderRef = storageRef.child(uid);
    }

    private Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        Log.d("picassoImageTarget", " picassoImageTarget");
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }

    */


}
