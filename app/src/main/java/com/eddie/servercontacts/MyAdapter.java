package com.eddie.servercontacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends BaseAdapter {

    private List<Contact> list;

    public void addContact(Contact contact){
        list.add(contact);
        notifyDataSetChanged();
    }

    public void updateContact(Contact contact){
        int pos = list.indexOf(contact);
        if(pos >= 0){
            list.set(pos, contact);
        }
        notifyDataSetChanged();
    }

    public void deleteContact(Contact contact){
        list.remove(contact);
        notifyDataSetChanged();
    }

    public MyAdapter(List<Contact> list) {
        this.list = list;
    }

    public MyAdapter() {
        this.list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if(position >= 0 && position < list.size()){
            return list.get(position);
        }else{
            return new Contact();
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_cell, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        TextView email = convertView.findViewById(R.id.email);
        TextView phone = convertView.findViewById(R.id.phone);

        Contact contact = (Contact) getItem(position);

        name.setText(contact.getName());
        email.setText(contact.getEmail());
        phone.setText(contact.getPhone());

        return convertView;
    }
}
