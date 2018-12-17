package com.example.joseph.famserver.Models;

import com.google.gson.Gson;

public class JsonModel {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
