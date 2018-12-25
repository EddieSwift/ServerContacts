package com.eddie.servercontacts;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpProvider {

    private Gson gson;
    private OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE_URL = "https://contacts-telran.herokuapp.com";
    private static final String AUTHORIZATION = "Authorization";
    public static final String MY_TAG = "MY_TAG";

    private static final HttpProvider ourInstance = new HttpProvider();

    public static HttpProvider getInstance() {
        return ourInstance;
    }

    private HttpProvider() {
        gson = new Gson();
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public String Registration(String email, String password) throws Exception {
        AuthDto authDto = new AuthDto(email, password);
        String json = gson.toJson(authDto);

        URL url = new URL(BASE_URL + "/api/registration");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "application/json");
        connection.setReadTimeout(15000);
        connection.setConnectTimeout(15000);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(json);
        bw.flush();
        bw.close();

        int code = connection.getResponseCode();
        String line = null;
        String res = "";

        if(code >= 200 && code < 300){
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            AuthResponseDto responseDto = gson.fromJson(sb.toString(), AuthResponseDto.class);
            return  responseDto.getToken();
        }else{
            InputStreamReader isr = new InputStreamReader(connection.getErrorStream());
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            if(code == 409){
                ErrorDto errorDto = gson.fromJson(sb.toString(), ErrorDto.class);
                throw new Exception(errorDto.getMessage());
            }else{
                Log.e(MY_TAG, "Registration error: " + sb.toString());
                throw new Exception("Connection error!");
            }

        }

    }

    public String login(String email, String password) throws Exception {
        AuthDto authDto = new AuthDto(email, password);
        String json = gson.toJson(authDto);

        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder().url(BASE_URL + "/api/login")
                .post(requestBody)
                .addHeader("Authorized", "token")
                .build();

        Response response = client.newCall(request).execute();
        String res = response.body().string();
        if(response.isSuccessful()){
            AuthResponseDto responseDto = gson.fromJson(res, AuthResponseDto.class);
            return responseDto.getToken();
        }else{
            if(response.code() == 401){
                throw new Exception("Wrong email or password!");
            }else{
                Log.d(MY_TAG, "login error: " + res);
                throw new Exception("Server error!");
            }
        }

    }

    public String clearContacts(String token) throws Exception {
        Request request = new Request.Builder().url(BASE_URL + "/api/clear")
                .addHeader(AUTHORIZATION, token)
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        String res = response.body().string();
        if(response.isSuccessful()) {
            DeleteResponse deleteResponse = gson.fromJson(res, DeleteResponse.class);
            return deleteResponse.getStatus();
        }else{
            ErrorDto error = gson.fromJson(res, ErrorDto.class);
            throwHttpError(response.code(), error.toString());
        }
        return "Done";
    }

    public ContactsDto getAllContacts(String token) throws Exception {
        Request request = new Request.Builder().url(BASE_URL + "/api/contact")
                .addHeader(AUTHORIZATION ,token)
                .build();

        Response response = client.newCall(request).execute();
        String res = response.body().string();
        if(!response.isSuccessful()){
            throwHttpError(response.code(), null);
        }
        return gson.fromJson(res, ContactsDto.class);
    }

    public Contact addContact(String token, Contact contact) throws Exception {
        String json = gson.toJson(contact);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(BASE_URL + "/api/contact")
                .addHeader(AUTHORIZATION, token)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        String res = response.body().string();
        if(!response.isSuccessful()){
            throwHttpError(response.code(), null);
        }
        return gson.fromJson(res, Contact.class);
    }

    public Contact updateContact(String token, Contact contact) throws Exception {
        RequestBody requestBody = RequestBody.create(JSON, gson.toJson(contact));
        Request request = new Request.Builder().url(BASE_URL + "/api/contact")
                .addHeader(AUTHORIZATION, token)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String res = response.body().string();
        if(!response.isSuccessful()){
            throwHttpError(response.code(), null);
        }
        return gson.fromJson(res, Contact.class);
    }

    public String deleteContact(String token, Contact contact) throws Exception {
        Request request = new Request.Builder().url(BASE_URL + "/api/contact/" + contact.getId())
                .addHeader(AUTHORIZATION, token)
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        String res = response.body().string();
        if(response.isSuccessful()) {
            DeleteResponse deleteResponse = gson.fromJson(res, DeleteResponse.class);
            return deleteResponse.getStatus();
        }else{
            ErrorDto error = gson.fromJson(res, ErrorDto.class);
            throwHttpError(response.code(), error.toString());
            return  error.getMessage();
        }
    }

    private void throwHttpError(int code, String details) throws Exception {
        details = details == null ? "" : " (" + details + ")";
        switch (code) {
            case 401:
                throw new Exception("Unauthorized" + details);
            case 403:
                throw new Exception("Forbidden" + details);
            case 404:
                throw new Exception("Not found" + details);
            default:
                throw new Exception("Something error" + details);
        }
    }


}

