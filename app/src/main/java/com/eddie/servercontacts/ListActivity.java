package com.eddie.servercontacts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends WaitingActivity {

    private static final int CONTACT_ACTIVITY_CODE = 1;

    private ListView contactList;
    private MenuItem addItem, exitItem;
    private MyAdapter adapter;
    private Gson gson = new Gson();
    public static final String MY_TAG = "MY_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        wrapToWaitingActivity(findViewById(R.id.main));

        contactList = findViewById(R.id.contact_list);

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = (Contact) adapter.getItem(position);
                Intent intent = new Intent(parent.getContext(), ContactActivity.class);
                intent.putExtra(ContactActivity.PARAM_MODE, ContactActivity.EDIT_MODE);
                intent.putExtra(ContactActivity.PARAM_CONTACT_JSON, gson.toJson(contact));
                startActivityForResult(intent, CONTACT_ACTIVITY_CODE);
            }
        });

        new WebRefreshContactList().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.exit){
            StoreProvider.getInstance().clearToken();
            setResult(RESULT_OK);
            finish();
        }else if(item.getItemId() == R.id.add){
            Intent intent = new Intent(this, ContactActivity.class);
            intent.putExtra(ContactActivity.PARAM_MODE, ContactActivity.ADD_MODE);
            startActivityForResult(intent, CONTACT_ACTIVITY_CODE);
        }else if(item.getItemId() == R.id.delete_all){
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure?")
                    .setCancelable(false)
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new ClearContacts().execute();
                        }
                    }).create().show();
        }else if(item.getItemId() == R.id.refresh){
            new WebRefreshContactList().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == CONTACT_ACTIVITY_CODE){
            int action = data.getIntExtra(ContactActivity.PARAM_ACTION, -1);
            Log.d(MY_TAG, "action: " + action);
            Contact contact = gson.fromJson(data.getStringExtra(ContactActivity.PARAM_CONTACT_JSON), Contact.class);
            if(action == ContactActivity.ACTION_ADD){
                adapter.addContact(contact);
            }else if(action == ContactActivity.ACTION_UPDATE){
                adapter.updateContact(contact);
            }else if(action == ContactActivity.ACTION_DELETE){
                adapter.deleteContact(contact);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class WebRefreshContactList extends AsyncTask<Void, Void, String>{

        private boolean isSuccessful = true;
        private List<Contact> list;

        public WebRefreshContactList(){
            list = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {

            setWaitingMode();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String token = StoreProvider.getInstance().getToken();
                ContactsDto contactsDto = HttpProvider.getInstance().getAllContacts(token);
                Log.d(MY_TAG, "contactsDto: " + contactsDto);
                list = contactsDto.getContacts();
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
            if(isSuccessful) {
                adapter = new MyAdapter(list);
                contactList.setAdapter(adapter);
            }else{
                showError(s);
            }
        }
    }

    private class ClearContacts extends AsyncTask<Void, Void, String>{

        private boolean isSuccessful = true;

        @Override
        protected void onPreExecute() {
            setWaitingMode();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String token = StoreProvider.getInstance().getToken();
                return HttpProvider.getInstance().clearContacts(token);
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
                Toast.makeText(ListActivity.this, s, Toast.LENGTH_SHORT).show();
            }else{
                showError(s);
            }
        }
    }

}
