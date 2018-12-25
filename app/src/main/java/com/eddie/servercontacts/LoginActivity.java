package com.eddie.servercontacts;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends WaitingActivity implements View.OnClickListener {

    private EditText inputEmail, inputPassword;
    private Button regBtn, loginBtn;
    private static final int LIST_ACTIVITY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        wrapToWaitingActivity(findViewById(R.id.main));

//        if(StoreProvider.getInstance().getToken() != null){
//            openNextActivity();
//        }

        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        regBtn = findViewById(R.id.reg_btn);
        loginBtn = findViewById(R.id.login_btn);

        regBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.reg_btn){
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            new RegistrationTask(email, password).execute();
        }else if(v.getId() == R.id.login_btn){
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            new LoginTask(email, password).execute();
        }
    }

    private void openNextActivity(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivityForResult(intent, LIST_ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_CANCELED && requestCode == LIST_ACTIVITY_CODE){
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class RegistrationTask extends AsyncTask<Void, Void, String>{

        private String email;
        private String password;
        private boolean isSuccessful;

        public RegistrationTask(String email, String password) {
            this.email = email;
            this.password = password;
            isSuccessful = true;
        }

        @Override
        protected void onPreExecute() {
            setWaitingMode();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpProvider.getInstance().Registration(email, password);
                return "login ok!";
            } catch (Exception e) {
                e.printStackTrace();
                isSuccessful = false;
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            desetWaitingMode();
            if(isSuccessful){
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
            }else{
                showError(s);
            }
        }
    }

    private class LoginTask extends AsyncTask<Void, Void, String>{

        private String email;
        private String password;
        private boolean isSuccessful;

        public LoginTask(String email, String password) {
            this.email = email;
            this.password = password;
            isSuccessful = true;
        }

        @Override
        protected void onPreExecute() {
            setWaitingMode();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String token = HttpProvider.getInstance().login(email, password);
                StoreProvider.getInstance().saveToket(token);
                return "Done";
            } catch (Exception e) {
                e.printStackTrace();
                isSuccessful = false;
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            desetWaitingMode();
            if(isSuccessful){
                openNextActivity();
            }else{
                showError(s);
            }
        }
    }

}

