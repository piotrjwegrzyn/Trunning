package com.example.Trunning;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestowaLista extends AppCompatActivity {

    ListView Test_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testowa_lista);

        Test_list = findViewById(R.id.Test_List);
        Dane dane = Dane.getInstance();

        Test_list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dane.getPoints()));
    }
}