package com.hardy.imageuploadwithcoordinates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    TextView textViewCo;
    Button button;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int locationRequestID = 22, cameraID = 44, galleryID = 55;
    ImageView imageView,uploadedImageView;
    String sLatitude = "", sLongitude = "";
    Bitmap bitmap;
    Uri selectImageUri;
    File file;
    ImageButton imageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        textViewCo = findViewById(R.id.textView);
        button = findViewById(R.id.bTnnn);
        imageView = findViewById(R.id.imgeview);
        uploadedImageView=findViewById(R.id.uploadedImage);
        imageButton=findViewById(R.id.imageButton);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Open Camera", "Open Gallery", "Cancel"};
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Select Option");
                alertDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (options[i].equals("Open Camera")) {
                            dialogInterface.dismiss();
                            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(camera_intent, cameraID);
                        } else if (options[i].equals("Open Gallery")) {
                            dialogInterface.dismiss();
                            Intent gallery_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(gallery_intent, galleryID);
                        } else if (options[i].equals("Cancel")) {
                            dialogInterface.dismiss();

                        }
                    }
                });
                alertDialog.show();


            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Open Camera", "Open Gallery", "Cancel"};
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Select Option");
                alertDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (options[i].equals("Open Camera")) {
                            dialogInterface.dismiss();
                            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(camera_intent, cameraID);
                        } else if (options[i].equals("Open Gallery")) {
                            dialogInterface.dismiss();
                            Intent gallery_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(gallery_intent, galleryID);
                        } else if (options[i].equals("Cancel")) {
                            dialogInterface.dismiss();

                        }
                    }
                });
                alertDialog.show();


            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectImageUri==null)
                {
                    Toast.makeText(MainActivity.this, "Select/ Capture  an Image", Toast.LENGTH_SHORT).show();
                return;
                }
                getCurrentLocation();


            }
        });

    }

    private void postData(String sLatitude, String sLongitude) {
        if (sLatitude.trim().isEmpty() || sLongitude.trim().isEmpty()) {
            requestNewLocationData();
        } else {
            Toast.makeText(MainActivity.this, "inside post" + sLatitude + "\n" + sLongitude, Toast.LENGTH_LONG).show();


            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part filetoUpload = MultipartBody.Part.createFormData("filename", file.getName(), requestBody);


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://staging.bookchor.com/TestAssignment/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.uploadImage(sLatitude, sLongitude, filetoUpload);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(MainActivity.this, String.valueOf(response.body()), Toast.LENGTH_LONG).show();
                       JsonObject object=response.body();
                       try{
                           JSONObject object1=new JSONObject(String.valueOf(object));
                           String uUri=object1.getString("image_path");
                           String longT=object1.getString("latitude");
                           String latiT=object1.getString("longitude");
                           Toast.makeText(MainActivity.this, uUri+"\n"+longT+"\n"+latiT+"\n", Toast.LENGTH_LONG).show();
                           textViewCo.setText("Longitude : " + longT+ "\n" + "Latitude :" +latiT);
                           Glide.with(MainActivity.this)
                                   .load(uUri)
                                   .into(uploadedImageView);


                       }
                       catch (Exception e){


                       }

                         }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                    Toast.makeText(MainActivity.this, String.valueOf(t.getMessage()), Toast.LENGTH_LONG).show();

                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == cameraID) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            Bitmap tempImage = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true);
            String path = MediaStore.Images.Media.insertImage(getApplication().getContentResolver(), tempImage, "demoImage", null);
            selectImageUri = Uri.parse(path);


        }
        if (requestCode == galleryID && resultCode == RESULT_OK) {
            selectImageUri = data.getData();
            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(selectImageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (Exception ex) {
            }


        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream);
        //String image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        file = new File(getPathFromUri(selectImageUri));


    }

    private String getPathFromUri(Uri selectImageUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectImageUri, projection, null, null, null);
        if (cursor == null) {
            return selectImageUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationRequestID && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }




    }

    private void getCurrentLocation() {
        if (checkPermission()) {
            if (checkLocationEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
//                            textView.setText("longitude : " + String.valueOf(location.getLongitude()) + "\n" + "longitude :" + String.valueOf(location.getLatitude()));
                            sLatitude = new String(String.valueOf(location.getLatitude()));
                            sLongitude = new String(String.valueOf(location.getLongitude()));
                            postData(sLatitude, sLongitude);

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermission();
        }

    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mlocationrequest = new LocationRequest();
        mlocationrequest.setInterval(5);
        mlocationrequest.setFastestInterval(0);
        mlocationrequest.setNumUpdates(1);
        mlocationrequest.setPriority(mlocationrequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(mlocationrequest, mLocationCallback, Looper.getMainLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location mLocation = locationResult.getLastLocation();

           // textView.setText("longitude : " + String.valueOf(mLocation.getLongitude()) + "\n" + "longitude :" + String.valueOf(mLocation.getLatitude()));
            sLatitude = String.valueOf(mLocation.getLatitude());
            sLongitude = String.valueOf(mLocation.getLongitude());
            postData(sLatitude, sLongitude);

        }
    };

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, locationRequestID);
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


}