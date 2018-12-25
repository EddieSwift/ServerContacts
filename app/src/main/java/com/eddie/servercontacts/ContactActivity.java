package com.eddie.servercontacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


public class ContactActivity extends WaitingActivity {

    public static final String PARAM_MODE = "MODE";
    public static final int ADD_MODE = 1;
    public static final int EDIT_MODE = 2;
    public static final String PARAM_CONTACT_JSON = "PARAM_CONTACT_JSON";
    public static final String PARAM_ACTION = "ACTION";
    public static final int ACTION_ADD = 1;
    public static final int ACTION_UPDATE = 2;
    public static final int ACTION_DELETE = 3;
    public static final String MY_TAG = "MY_TAG";

    private LinearLayout viewWrapper, editWrapper;
    private MenuItem itemSave, itemEdit, itemDelete;
    private TextView nameView, lastNameView, emailView, phoneView, addressView, descriptionView, idView;
    private EditText nameEdit, lastNameEdit, emailEdit, phoneEdit, addressEdit, descriptionEdit;
    private int mode;
    private Contact contact;
    private Gson gson;

    private boolean activityCreated = false;
    private boolean menuCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        wrapToWaitingActivity(findViewById(R.id.main));

        viewWrapper = findViewById(R.id.view_wrapper);
        nameView = findViewById(R.id.name_view);
        lastNameView = findViewById(R.id.last_name_view);
        emailView = findViewById(R.id.email_view);
        phoneView = findViewById(R.id.phone_view);
        addressView = findViewById(R.id.address_view);
        descriptionView = findViewById(R.id.description_view);
        idView = findViewById(R.id.id_view);

        editWrapper = findViewById(R.id.edit_wrapper);
        nameEdit = findViewById(R.id.name_edit);
        lastNameEdit = findViewById(R.id.last_name_edit);
        emailEdit = findViewById(R.id.email_edit);
        phoneEdit = findViewById(R.id.phone_edit);
        addressEdit = findViewById(R.id.address_edit);
        descriptionEdit = findViewById(R.id.description_edit);

        gson = new Gson();
        Intent intent = getIntent();
        mode = intent.getIntExtra(PARAM_MODE, EDIT_MODE);
        if(mode == ADD_MODE){
            contact = new Contact();
        }else {
            contact = gson.fromJson(intent.getStringExtra(PARAM_CONTACT_JSON), Contact.class);
        }

        activityCreated = true;
        initViewMode();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        itemSave = menu.findItem(R.id.item_save);
        itemEdit = menu.findItem(R.id.item_edit);
        itemDelete = menu.findItem(R.id.item_delete);
        itemDelete.setVisible(false);
        menuCreated = true;
        initViewMode();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mode == ADD_MODE){
            if(item.getItemId() == R.id.item_save){
                setValuesToContact();
                new WebAddContact().execute();
            }
        }else if(mode == EDIT_MODE){
            if(item.getItemId() == R.id.item_save){
                setValuesToContact();
                new WebUpdateContact().execute();
            }else if(item.getItemId() == R.id.item_edit){
                setEditMode();
            }else if(item.getItemId() == R.id.item_delete){
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setValuesToContact();
                                new WebDeleteContact().execute();
                            }
                        }).create().show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private synchronized void initViewMode(){
        if(activityCreated && menuCreated){
            if(mode == ADD_MODE){
                setEditMode();
            }else{
                setViewMode();
            }
        }
    }

    private void setEditMode(){
        viewWrapper.setVisibility(View.GONE);
        editWrapper.setVisibility(View.VISIBLE);
        itemSave.setVisible(true);
        itemEdit.setVisible(false);
        itemDelete.setVisible(mode == EDIT_MODE);

        nameEdit.setText(checkNull(contact.getName()));
        lastNameEdit.setText(checkNull(contact.getLastName()));
        emailEdit.setText(checkNull(contact.getEmail()));
        phoneEdit.setText(checkNull(contact.getPhone()));
        addressEdit.setText(checkNull(contact.getAddress()));
        descriptionEdit.setText(checkNull(contact.getDescription()));
    }

    private void setViewMode(){
        viewWrapper.setVisibility(View.VISIBLE);
        editWrapper.setVisibility(View.GONE);
        itemSave.setVisible(false);
        itemEdit.setVisible(true);
        itemDelete.setVisible(false);

        nameView.setText(checkNull(contact.getName()));
        lastNameView.setText(checkNull(contact.getLastName()));
        emailView.setText(checkNull(contact.getEmail()));
        phoneView.setText(checkNull(contact.getPhone()));
        addressView.setText(checkNull(contact.getAddress()));
        descriptionView.setText(checkNull(contact.getDescription()));
        idView.setText(String.valueOf(contact.getId()));
    }

    private void setValuesToContact(){
        contact.setName(nameEdit.getText().toString());
        contact.setLastName(lastNameEdit.getText().toString());
        contact.setEmail(emailEdit.getText().toString());
        contact.setPhone(phoneEdit.getText().toString());
        contact.setAddress(addressEdit.getText().toString());
        contact.setDescription(descriptionEdit.getText().toString());
    }

    private String checkNull(String s){
        return s == null ? "" : s;
    }

    private class WebAddContact extends AsyncTask<Void, Void, String>{

        private boolean isSuccessful = true;

        @Override
        protected void onPreExecute() {
            setWaitingMode();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String token = StoreProvider.getInstance().getToken();
                contact = HttpProvider.getInstance().addContact(token, contact);
                return "Done";
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(MY_TAG, "don't added " + e.getMessage());
                isSuccessful = false;
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            desetWaitingMode();
            if(isSuccessful){
                Log.d(MY_TAG, "added: " + contact.toString());
                Intent intent = new Intent();
                intent.putExtra(PARAM_CONTACT_JSON, new Gson().toJson(contact));
                intent.putExtra(PARAM_ACTION, ACTION_ADD);
                setResult(RESULT_OK, intent);
                finish();
            }else{
                showError(s);
            }
        }
    }

    private class WebUpdateContact extends AsyncTask<Void, Void, String>{

        private boolean isSuccessful = true;

        @Override
        protected void onPreExecute() {
            setWaitingMode();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                String token = StoreProvider.getInstance().getToken();
                contact = HttpProvider.getInstance().updateContact(token, contact);
                return "Updated";
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
                Intent intent = new Intent();
                intent.putExtra(PARAM_CONTACT_JSON, new Gson().toJson(contact));
                intent.putExtra(PARAM_ACTION, ACTION_UPDATE);
                setResult(RESULT_OK, intent);
                setViewMode();
                Toast.makeText(ContactActivity.this, s, Toast.LENGTH_SHORT).show();
            }else{
                showError(s);
            }
        }
    }

    private class WebDeleteContact extends AsyncTask<Void, Void, String>{

        private boolean isSuccessful = true;

        @Override
        protected void onPreExecute() {
            setWaitingMode();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String token = StoreProvider.getInstance().getToken();
                String status = HttpProvider.getInstance().deleteContact(token, contact);
                return status;
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
                Intent intent = new Intent();
                intent.putExtra(PARAM_ACTION, ACTION_DELETE);
                intent.putExtra(PARAM_CONTACT_JSON, gson.toJson(contact));
                setResult(RESULT_OK, intent);
                finish();
            }else{
                showError(s);
            }
        }
    }


}

