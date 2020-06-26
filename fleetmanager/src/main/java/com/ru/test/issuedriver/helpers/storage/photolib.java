package com.ru.test.issuedriver.helpers.storage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.ru.test.issuedriver.BuildConfig;
import com.ru.test.issuedriver.MyActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

public class photolib {
    private static final String TAG = "myLogs";
    private static boolean isNew;
    private static AppCompatActivity activity;
    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private static final int RESULT_LOAD_IMG = 1003;
    private static Uri photoURI;
    private static File photoFile;

    public static void init(AppCompatActivity _activity){
        activity = _activity;

    }

    public static void getPhotoFromCamera(boolean _isNew) {
        if(!checkCameraHardware(activity))
            return; // камера недоступна

        isNew = _isNew;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) { //(IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, ex.getLocalizedMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                photoURI = FileProvider.getUriForFile(activity,
                         BuildConfig.APPLICATION_ID,
                        photoFile);

                try {
                    String ddd = fbStorageUploads.getFilePath(activity, photoURI);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private static String currentPhotoPath;

    private static File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /** Check if this device has a camera */
    private static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    private static void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        File f = new File(currentPhotoPath);
        Uri contentUri = FileProvider.getUriForFile(activity,  BuildConfig.APPLICATION_ID, f);

        //Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    public static void getPhotoFromGalary(boolean _isNew){
        isNew = _isNew;
        final Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        Runnable run = new Runnable() {
            @Override
            public void run() {
                activity.startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        };
        Thread thread = new Thread(run);
        thread.start();

    }

    public static Uri onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE
                && resultCode == activity.RESULT_OK) {
            Bitmap imageBitmap = null;
            if(data != null) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                // не используется


                return Uri.EMPTY;
            }
            else {
                if(photoFile != null
                        && photoFile.exists()) {
                    //imageBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    galleryAddPic();
                    docItem tmp = new docItem(photoURI, "");

                    if (isNew) {
                        fbStorageUploads.uploadFromFile(MyActivity.getMyInstance(), tmp);
                        //AnalizePage2Fragment.getInstance().initData(tmp);
                        //AnalizeContainerFragment.viewPager.setCurrentItem(1, true);
                        //Intent intent = new Intent(activity, IssueActivity.class);
                        //intent.putExtra("photo", photoURI);
                        //activity.startActivity(intent);
                        //activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        return photoURI;
                    }
                    else {
                        fbStorageUploads.uploadFromFile(MyActivity.getMyInstance(), tmp);
                        //AnalizePage2Fragment.getInstance().addData(tmp);
                        return photoURI;
                    }
                }
            }
            //if(imageBitmap!=null) {
            //    imageView.setImageBitmap(imageBitmap);
            //}
        } else if (requestCode == RESULT_LOAD_IMG
                && resultCode == activity.RESULT_OK
                && null != data) {
            //try {
                final Uri imageUri = data.getData();
                docItem tmp = new docItem(imageUri, "");
                if(isNew) {
                    fbStorageUploads.uploadFromFile(MyActivity.getMyInstance(), tmp);
                    //AnalizePage2Fragment.getInstance().initData(tmp);
                    //AnalizeContainerFragment.viewPager.setCurrentItem(1, true);
                    //Intent intent = new Intent(activity, IssueActivity.class);
                    //intent.putExtra("photo", imageUri);
                    //activity.startActivity(intent);
                    //activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                    return imageUri;
                }
                else {
                    fbStorageUploads.uploadFromFile(MyActivity.getMyInstance(), tmp);
                    //AnalizePage2Fragment.getInstance().addData(tmp);
                    return imageUri;
                }
                /*final InputStream imageStream = activity.getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView2.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
             }
                 */
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE
                || requestCode == RESULT_LOAD_IMG)
            return Uri.EMPTY;
        return null;
    }

    public static Bitmap getBitmapFromUri(Uri imageUri){
        InputStream imageStream = null;
        try {
            imageStream = activity.getContentResolver().openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(imageStream == null)
            return null;

        return rotateImage(imageUri, BitmapFactory.decodeStream(imageStream));
    }

    public static Bitmap rotateImage(Uri uri, Bitmap bitmap){
        ExifInterface exifInterface = null;

        try {
            File file = new File(uri.getPath());
            Uri photoURI2 = FileProvider.getUriForFile(activity,
                        BuildConfig.APPLICATION_ID,
                        file);

            file = new File(photoURI2.getPath());

            exifInterface = new ExifInterface(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
                break;
        }
        return Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    public static Bitmap rotateImage(Bitmap roratedBitmap){
        /*
        ExifInterface exifInterface = null;

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
                break;
        }


        Bitmap roratedBitmap = Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);



         ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int quality = 100;
        if(roratedBitmap.getAllocationByteCount() > 5000000)
            quality = 80;
        if(roratedBitmap.getAllocationByteCount() > 25000000)
            quality = 65;
        if(roratedBitmap.getAllocationByteCount() > 50000000)
            quality = 50;

        roratedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bitmapdata = baos.toByteArray();

*/
        int width = roratedBitmap.getWidth();
        int height = roratedBitmap.getHeight();
        float scaleWidth = ((float) 400) / width;
        float scaleHeight = ((float) 400) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                roratedBitmap, 0, 0, width, height, matrix, false);
        roratedBitmap.recycle();

        return resizedBitmap;

       // Bitmap bmp = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
       // return bmp.copy(Bitmap.Config.ARGB_8888, true);
    }

    /*
    private static File createImageFile2(){
        String name = "1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = activity.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + IMAGES_FOLDER_NAME);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            return  new File("");
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + IMAGES_FOLDER_NAME;

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            return new File(imagesDir, name + ".jpg");
        }
    }

    private static String IMAGES_FOLDER_NAME = "HEYDOC";
    private static void saveImage(Bitmap bitmap, @NonNull String name) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = activity.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + IMAGES_FOLDER_NAME);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + IMAGES_FOLDER_NAME;

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".jpg");
            fos = new FileOutputStream(image);

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }
*/



}
