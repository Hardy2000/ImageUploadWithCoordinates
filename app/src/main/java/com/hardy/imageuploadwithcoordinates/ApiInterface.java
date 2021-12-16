package com.hardy.imageuploadwithcoordinates;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("upload_image.php")
    @Multipart
    Call<JsonObject> uploadImage(@Query("latitude") String latitude, @Query("longitude") String longitude, @Part MultipartBody.Part file);

}
