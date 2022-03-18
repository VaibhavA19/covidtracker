package com.valreja.covidtracker;

import com.valreja.covidtracker.DataBase.DBEntry;

import org.json.JSONArray;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface API {
    @Multipart
    // POST request to upload an image from storage
    @POST("/")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/uploaddb")
    Call<JSONArray> uploadDB(@Body List<DBEntry> db);
}
