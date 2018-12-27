package com.sheygam.masa_2018_g1_retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Api {

    @POST("api/registration")
    Call<AuthResponse> registration(@Body Auth auth);

    @POST("api/login")
    Call<AuthResponse> login(@Body Auth auth);


    @GET("api/contact")
    Call<ContactsResponse> getAllContacts(@Header("Authorization") String token);
}
