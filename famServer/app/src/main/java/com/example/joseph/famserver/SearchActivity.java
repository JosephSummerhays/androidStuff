package com.example.joseph.famserver;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import com.example.joseph.famserver.Models.ItemModel;

import java.util.ArrayList;

public class SearchActivity extends Activity {
    private RecyclerView searchResults;
    private SearchView searchQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchResults = findViewById(R.id.search_results);
        searchQuery = findViewById(R.id.search_query);
        searchQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchResults.setAdapter(new ItemRecyclerAdapter(StaticGlobals.searchFor(s)));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchResults.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<ItemModel>  empty = new ArrayList<>();
        empty.add(new ItemModel("Search Results Are empty","",R.drawable.event));
        searchResults.setAdapter(new ItemRecyclerAdapter(empty));
    }
}
