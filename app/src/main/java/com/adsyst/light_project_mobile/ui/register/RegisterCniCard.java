package com.adsyst.light_project_mobile.ui.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.config.Global;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.web_service.ApiService;
import com.adsyst.light_project_mobile.web_service.RetrofitBuilder;
import com.adsyst.light_project_mobile.web_service.model.RegisterResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class RegisterCniCard extends AppCompatActivity {

    private static final String TAG = "RegisterCniCard";
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Toolbar toolbar;

    private ProgressDialog progressDialog;
    private Context context;

    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private Call<RegisterResponse> call;


    private Button GetImageFromGalleryButton, UploadImageOnServerButton;
    private ImageView ShowSelectedImageRecto, ShowSelectedImageVerso;
    private Bitmap FixBitmap, FixBitmap2;
    private String ImageTagRecto = "nom_piece_recto";
    private String ImageNameRecto = "piece_recto";
    private String ImageTagVerso = "nom_piece_verso";
    private String ImageNameVerso = "piece_verso";
    private ByteArrayOutputStream byteArrayOutputStream1, byteArrayOutputStream2;
    private byte[] byteArray1, byteArray2;
    private String ConvertImageRecto, ConvertImageVerso;
    private String nom, prenom, genre, tel, cni, email, password, start, end, roleSelected, GetImageNameFromRectoIdCard, GetImageNameFromVersoIdCard;
    private LinearLayout imgCardVerso;
    private TextView infoNom, infoPrenom, infoCni;
    private RelativeLayout dividerBarUpload;
    private int GALLERY = 1, CAMERA = 2;
    private final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;

    private String imagePath, imagePath2;
    private MultipartBody.Part getImgRecto, getImgVerso;

    private int counter = 0;
    private Uri fileUri; // file url to store image/video

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_cni_card);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.register2));
        toolbar.setSubtitle(getString(R.string.subTitle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initItem();
    }

    private void initItem() {
        context = this;
        //récupération des vues en lien dans notre activity
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        progressDialog = new ProgressDialog(context);

        Intent intent = getIntent();
        nom = intent.getStringExtra("NOM");
        prenom = intent.getStringExtra("PRENOM");
        genre = intent.getStringExtra("GENRE");
        tel = intent.getStringExtra("TELEPHONE");
        cni = intent.getStringExtra("CNI");
        email = intent.getStringExtra("EMAIL");
        password = intent.getStringExtra("PASSWORD");
        start = intent.getStringExtra("START");
        end = intent.getStringExtra("END");
        roleSelected = intent.getStringExtra("ROLE");

        GetImageFromGalleryButton = (Button) findViewById(R.id.buttonSelect);
        UploadImageOnServerButton = (Button) findViewById(R.id.buttonUpload);
        ShowSelectedImageRecto = (ImageView) findViewById(R.id.imageViewRecto);
        ShowSelectedImageVerso = (ImageView) findViewById(R.id.imageViewVerso);
        imgCardVerso = (LinearLayout) findViewById(R.id.imgCardVerso);
        infoNom = (TextView) findViewById(R.id.infoNom);
        infoPrenom = (TextView) findViewById(R.id.infoPrenom);
        infoCni = (TextView) findViewById(R.id.infoCni);
        dividerBarUpload = (RelativeLayout) findViewById(R.id.dividerBarUpload);


        infoNom.setText(nom);
        infoPrenom.setText(prenom);

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

        //write external storage
        checkForPermissions();

        if (ContextCompat.checkSelfPermission(RegisterCniCard.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }


        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPictureDialog();
            }
        });

        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImagesToServerLIGHT();
            }
        });

    }

    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

   private void checkForPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                // This part I didn't implement,because for my case it isn't needed
                Log.i(TAG,"Unexpected flow");
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission is already granted, call the function that does what you need

            onFileWritePermissionGranted();
        }
    }

    void onFileWritePermissionGranted() {
        // Write to some file
    }


    private void uploadImagesToServerLIGHT() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(getString(R.string.connexionserver));
                progressDialog.setTitle(getString(R.string.etape1EnvoiDesDonnees));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }
        });

        //initImage();
        initImageUpload();
        //initImageUploadCamera();

        //RequestBody key_api1 = RequestBody.create(MultipartBody.FORM, Global.API_KEY);

        RequestBody key_api1 = RequestBody.create(MediaType.parse("text/plain"), Global.API_KEY);
        RequestBody nom1 = RequestBody.create(MediaType.parse("text/plain"), nom);
        RequestBody prenom1 = RequestBody.create(MediaType.parse("text/plain"), prenom);
        RequestBody start1 = RequestBody.create(MediaType.parse("text/plain"), start);
        RequestBody end1 = RequestBody.create(MediaType.parse("text/plain"), end);
        RequestBody genre1 = RequestBody.create(MediaType.parse("text/plain"), genre.toLowerCase());
        RequestBody cni1 = RequestBody.create(MediaType.parse("text/plain"), cni);
        RequestBody password1 = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody email1 = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody tel1 = RequestBody.create(MediaType.parse("text/plain"), tel);
        RequestBody roleSelected1 = RequestBody.create(MediaType.parse("text/plain"), roleSelected);


        call = service.add_client(key_api1, nom1, prenom1, start1, end1, genre1, cni1, password1, email1, tel1, roleSelected1, getImgRecto, getImgVerso);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                Log.w(TAG, "LIGHT onResponse: " + response);
                progressDialog.dismiss();

                assert response.body() != null;
                if(response.body().getMessage() != null){
                    Global.responseRegister(context, getString(R.string.register), response.body().getMessage(), R.drawable.ic_baseline_add_circle_outline_24, tel);
                }else {
                    Global.errorResponse(context, getString(R.string.register), "unne erreur est survenue");
                }

            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.w(TAG, "LIGHT onFailure " + t.getMessage());
                Global.errorResponse(context, getString(R.string.register), t.getMessage());
            }
        });
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getString(R.string.selectAction));
        String[] pictureDialogItems = {
                getString(R.string.tofGallery),
                getString(R.string.camera)};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                    takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {
        /*Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);*/

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY);

        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, GALLERY);*/

    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {

            if (data != null) {
                Uri contentURI = data.getData();

                if (ShowSelectedImageRecto.getDrawable() == null) {
                    //FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    imagePath = getRealPathFromUri(contentURI);
                    //debut compression
                    //FixBitmap = Bitmap.createScaledBitmap(FixBitmap, 60, 60, true);
                    //fin compression
                    ShowSelectedImageRecto.setImageURI(contentURI);
                    imgCardVerso.setVisibility(View.VISIBLE);
                    dividerBarUpload.setVisibility(View.VISIBLE);
                    return;
                }

                if (ShowSelectedImageRecto.getDrawable() != null) {
                    //FixBitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    imagePath2 = getRealPathFromUri(contentURI);
                    ShowSelectedImageVerso.setImageURI(contentURI);
                    UploadImageOnServerButton.setVisibility(View.VISIBLE);
                    imgCardVerso.setVisibility(View.VISIBLE);
                    dividerBarUpload.setVisibility(View.VISIBLE);
                    GetImageFromGalleryButton.setVisibility(View.GONE);
                }

            }

        } else if (requestCode == CAMERA) {

            if (ShowSelectedImageRecto.getDrawable() == null) {
                FixBitmap = (Bitmap) data.getExtras().get("data");
                //debut compression
                //FixBitmap = Bitmap.createScaledBitmap(FixBitmap, 60, 60, true);
                //fin compression
                ShowSelectedImageRecto.setImageBitmap(FixBitmap);
                imgCardVerso.setVisibility(View.VISIBLE);
                dividerBarUpload.setVisibility(View.VISIBLE);
                return;
            }


            if (ShowSelectedImageRecto.getDrawable() != null) {
                FixBitmap2 = (Bitmap) data.getExtras().get("data");
                //debut compression
                //FixBitmap2 = Bitmap.createScaledBitmap(FixBitmap2, 60, 60, true);
                //fin compression
                ShowSelectedImageVerso.setImageBitmap(FixBitmap2);
                UploadImageOnServerButton.setVisibility(View.VISIBLE);
                imgCardVerso.setVisibility(View.VISIBLE);
                dividerBarUpload.setVisibility(View.VISIBLE);
                GetImageFromGalleryButton.setVisibility(View.GONE);
            }
        }
    }

    private String getRealPathFromUri(Uri uri){
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_idx);
        cursor.close();
        return result;
    }

    private void initImageUpload() {
        File file = new File(imagePath);
        File file2 = new File(imagePath2);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        getImgRecto = MultipartBody.Part.createFormData("photo_1", file.getName(), requestBody);

        RequestBody requestBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);
        getImgVerso = MultipartBody.Part.createFormData("photo_2", file2.getName(), requestBody2);
    }


    private void initImageUploadCamera() {
        File file = savebitmap(FixBitmap);
        File file2 = savebitmap2(FixBitmap2);

        Log.w(TAG, "initImageUploadCamera--------------------: " + FixBitmap );
        Log.w(TAG, "initImageUploadCamera:--------------- " + file );

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        getImgRecto = MultipartBody.Part.createFormData("photo_1", file.getName(), requestBody);

        RequestBody requestBody2 = RequestBody.create(MediaType.parse("image/*"), file2);
        getImgVerso = MultipartBody.Part.createFormData("photo_2", file2.getName(), requestBody2);

        //MultipartBody.Part filePart = MultipartBody.Part.createFormData("photo", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));


    }

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        // String temp = null;
        File file = new File(extStorageDirectory, "temp.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "temp.png");

        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    private File savebitmap2(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        // String temp = null;
        File file = new File(extStorageDirectory, "temp2.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "temp2.png");

        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    private void initImage() {
        byteArrayOutputStream1 = new ByteArrayOutputStream();
        byteArrayOutputStream2 = new ByteArrayOutputStream();

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream1);
        FixBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream2);

        byteArray1 = byteArrayOutputStream1.toByteArray();
        byteArray2 = byteArrayOutputStream2.toByteArray();

        ConvertImageRecto = Base64.encodeToString(byteArray1, Base64.DEFAULT);
        ConvertImageVerso = Base64.encodeToString(byteArray2, Base64.DEFAULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 5:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Now user should be able to use camera
                } else {
                    Toast.makeText(RegisterCniCard.this, getString(R.string.impossibleUsingCamera), Toast.LENGTH_LONG).show();
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    // call the function that does what you need
                    onFileWritePermissionGranted();
                } else {
                    Log.e(TAG, "Write permissions has to be granted tp ATMS, otherwise it cannot operate properly.\n Exiting the program...\n");
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }

    }


    /**
     * attachBaseContext(Context newBase) methode callback permet de verifier la langue au demarrage de la page login
     *
     * @param newBase
     * @since 2020
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (call != null) {
            call.cancel();
            call = null;
        }
    }


    /*                    GESTION DU MENU DROIT                  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }*/

}