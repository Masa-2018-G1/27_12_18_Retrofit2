package com.sheygam.masa_2018_g1_retrofit;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Api api;
    private OkHttpClient client;
    private EditText inputEmail, inputPassword;
    private Button regBtn, getBtn;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        regBtn = findViewById(R.id.regBtn);
        getBtn = findViewById(R.id.getContactsBtn);

        regBtn.setOnClickListener(this);
        getBtn.setOnClickListener(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://contacts-telran.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        client = new OkHttpClient();
    }

    private void registration(String email, String password) throws IOException {

        Auth auth = new Auth(email,password);
        Call<AuthResponse> call = api.registration(auth);
        Response<AuthResponse> response = call.execute();
        if(response.isSuccessful()){
            AuthResponse token = response.body();
            this.token = token.getToken();
            Log.d("MY_TAG", "registration: " + token.getToken());
        }else if(response.code() == 409){
            String json = response.errorBody().string();
            Log.d("MY_TAG", "registration: " + json);
        }
    }

    private void login(String email, String password) throws IOException {

        Auth auth = new Auth(email,password);
        Call<AuthResponse> call = api.login(auth);
        Response<AuthResponse> response = call.execute();
        if(response.isSuccessful()){
            AuthResponse token = response.body();
            this.token = token.getToken();
            Log.d("MY_TAG", "registration: " + token.getToken());
        }else if(response.code() == 401){
            String json = response.errorBody().string();
            Log.d("MY_TAG", "registration: " + json);
        }
    }

    private void registrationOkHttp(String email, String password) throws IOException {
        Gson gson = new Gson();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        Auth auth = new Auth(email,password);
        String jsonRequest = gson.toJson(auth);

        RequestBody body = RequestBody.create(JSON,jsonRequest);

        Request request = new Request.Builder()
                .url("http://contacts-telran.herokuapp.com/api/registration/")
                .post(body)
                .build();

        okhttp3.Call call = client.newCall(request);

        okhttp3.Response response = call.execute();
        if(response.isSuccessful()){
            String jsonResponse = response.body().string();
            AuthResponse authResponse = gson.fromJson(jsonResponse,AuthResponse.class);
            Log.d("MY_TAG", "registrationOkHttp: " + authResponse.getToken());
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.regBtn){
            final String email = inputEmail.getText().toString();
            final String password = inputPassword.getText().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        login(email,password);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
//            asyncReg(email,password);
        }else if(v.getId() == R.id.getContactsBtn){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<ContactsResponse> response = api.getAllContacts(token).execute();
                        if(response.isSuccessful()){
                            ContactsResponse contacts = response.body();
                            for (Contact c : contacts.contacts){
                                Log.d("MY_TAG", "run: " + c);
                            }
                        }else{
                            Log.d("MY_TAG", "run: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void asyncReg(String email, String password){
        Auth auth = new Auth(email,password);
        api.registration(auth).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    if(response.isSuccessful()){
                        token = response.body().getToken();
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("Registration ok!")
                                .create()
                                .show();
                    }else{
                        try {
                            String error = response.errorBody().string();
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage(error)
                                    .create()
                                    .show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Connection Error!")
                        .create()
                        .show();

            }
        });
    }
}
