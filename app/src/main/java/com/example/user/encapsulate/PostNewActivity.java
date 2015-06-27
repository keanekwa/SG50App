package com.example.user.encapsulate;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PostNewActivity extends ActionBarActivity {

    private EditText mTitleEditText;
    private Spinner mCategorySpinner;
    private Button mPostButton;
    private Button mTakePhoto;
    private ProgressBar mLoading;

    private RelativeLayout mButtonsLayout;
    private RelativeLayout mImageLayout;
    private boolean isPicChosen;
    private Bitmap chosenPic;
    private ImageView chosenPicPrevew;
    private Button mRemoveButton;
    private Button mUploadButton;

    private static int TAKE_PHOTO_CODE = 21;
    private static int UPLOAD_PHOTO_CODE = 25;
    private File photoFile;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        mTitleEditText = (EditText)findViewById(R.id.captionEditText);
        mCategorySpinner = (Spinner)findViewById(R.id.categorySpinner);
        mPostButton = (Button)findViewById(R.id.finalizeButton);
        mLoading = (ProgressBar)findViewById(R.id.loadingProgressBar);
        mTakePhoto = (Button)findViewById(R.id.takePhotoButton);
        mButtonsLayout = (RelativeLayout)findViewById(R.id.noImgLL);
        mImageLayout = (RelativeLayout)findViewById(R.id.picChosenRL);
        chosenPicPrevew = (ImageView)findViewById(R.id.chosenImage);
        mRemoveButton = (Button)findViewById(R.id.removeButton);
        mUploadButton = (Button)findViewById(R.id.uploadPhotoButton);

        setPicChosen(false);

        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPicChosen){
                    setPicChosen(false);
                }
            }
        });

        mTakePhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(intent, TAKE_PHOTO_CODE);
                    }
                }
            }

        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, UPLOAD_PHOTO_CODE);
            }
        });

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(mCategorySpinner.getSelectedItem()).equals("Category")){
                    Toast.makeText(PostNewActivity.this, "Choose a category", Toast.LENGTH_LONG).show();
                }
                else if (!isPicChosen){
                    Toast.makeText(PostNewActivity.this, "Choose or take a picture", Toast.LENGTH_LONG).show();
                }
                else {
                    loading(true);
                    final ParseObject newPost = new ParseObject("allPostings");
                    newPost.put("createdBy", ParseUser.getCurrentUser().getUsername());
                    newPost.put("imgTitle", mTitleEditText.getText().toString());
                    newPost.put("likeNumber", 0);
                    switch (String.valueOf(mCategorySpinner.getSelectedItem())){
                        case "Best of the Past":
                            newPost.put("category", "BestOfPast");
                            break;
                        case "A Day as a Singaporean":
                            newPost.put("category", "DayAsSGean");
                            break;
                        case "Future Hopes for Singapore":
                            newPost.put("category", "FutureHopes");
                            break;
                        default:
                            newPost.put("category", "DayAsSGean");
                            break;
                    }

                    // Resize photo
                    Bitmap imageScaled = Bitmap.createScaledBitmap(chosenPic, 750, 750
                            * chosenPic.getHeight() / chosenPic.getWidth(), false);

                    /* Override Android default landscape orientation and save portrait
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap rotatedScaledImage = Bitmap.createBitmap(imageScaled, 0,
                            0, imageScaled.getWidth(), imageScaled.getHeight(),
                            matrix, true);*/

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    //rotatedScaledImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    imageScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                    byte[] scaledData = bos.toByteArray();

                    // Save the scaled image to Parse
                    String fileName = ParseUser.getCurrentUser().getUsername() + "_photo.jpg";
                    final ParseFile photoFile = new ParseFile(fileName, scaledData);
                    photoFile.saveInBackground(new SaveCallback() {

                        public void done(ParseException e) {
                            if (e != null) {
                                Toast.makeText(PostNewActivity.this,
                                        "Error saving: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                newPost.put("actualImage", photoFile);
                                newPost.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Toast.makeText(PostNewActivity.this, "Picture Uploaded!", Toast.LENGTH_LONG).show();
                                        Intent mainActIntent = new Intent(PostNewActivity.this, MainActivity.class);
                                        mainActIntent.putExtra("toRefreshPhotos", true);
                                        mainActIntent.putExtra("goto", 3);
                                        startActivity(mainActIntent);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed (){
        super.onBackPressed();
        Intent mainActIntent = new Intent(PostNewActivity.this, MainActivity.class);
        mainActIntent.putExtra("goto", 3);
        startActivity(mainActIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode==TAKE_PHOTO_CODE && resultCode==RESULT_OK){
            if(photoFile.exists()){
                addPicToGallery();
                chosenPic = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                setPicChosen(true);
            }
        }
        else if(requestCode==UPLOAD_PHOTO_CODE && resultCode==RESULT_OK){
            if(intent != null){
                Uri selectedImage = intent.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Some cursor magic to fetch the file
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                // Get byte array of Image after decoding the String
                chosenPic = BitmapFactory.decodeFile(imgDecodableString);
                setPicChosen(true);
            }
            else{
                Toast.makeText(PostNewActivity.this, "No picture chosen", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loading(Boolean toLoad){
        if(toLoad){
            mLoading.setVisibility(View.VISIBLE);
            mPostButton.setVisibility(View.INVISIBLE);
        }
        else{
            mLoading.setVisibility(View.INVISIBLE);
            mPostButton.setVisibility(View.VISIBLE);
        }
    }

    private void setPicChosen(Boolean isChosen){
        if(isChosen && chosenPic!=null){
            isPicChosen = true;
            chosenPicPrevew.setImageBitmap(chosenPic);
            mButtonsLayout.setVisibility(View.INVISIBLE);
            mImageLayout.setVisibility(View.VISIBLE);
        }
        else {
            mButtonsLayout.setVisibility(View.VISIBLE);
            mImageLayout.setVisibility(View.INVISIBLE);
            isPicChosen = false;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void addPicToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
