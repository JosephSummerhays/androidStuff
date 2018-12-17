package com.example.server;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Random;

public class dataGenerator {
    Gson g;
    Random r;
    public dataGenerator() {
        r = new Random();
        g = new Gson();
        try {
            fnames = g.fromJson(new FileReader(new File("src/main/java/json/fnames.json")), fnameData.class);
            mnames = g.fromJson(new FileReader(new File("src/main/java/json/mnames.json")), mnameData.class);
            snames = g.fromJson(new FileReader(new File("src/main/java/json/snames.json")), snameData.class);
            locations = g.fromJson(new FileReader(new File("src/main/java/json/locations.json")), locationData.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        if (r.nextBoolean()) {
            return getFemaleName();
        }
        return getMaleName();
    }

    public String getSurname() {
        int i = (r.nextInt() & Integer.MAX_VALUE) % snames.data.length;
        return snames.data[i];
    }
    public String getFemaleName() {
        int i = (r.nextInt() & Integer.MAX_VALUE) % fnames.data.length;
        return fnames.data[i];
    }
    public String getMaleName() {
        int i = (r.nextInt() & Integer.MAX_VALUE) % mnames.data.length;
        return mnames.data[i];
    }
    public locationData.location getLocation() {
        int i = (r.nextInt() & Integer.MAX_VALUE) % locations.data.length;
        return locations.data[i];
    }
    private class fnameData {
        public String data[];
    }
    private class mnameData {
        public String data[];
    }
    private class snameData {
        public String data[];
    }
    public class locationData {
        public class location {
            public String country;
            public String city;
            public float latitude;
            public float longitude;
        }
        location data[];
    }
    public String toString() {
        return fnames.data[0];
    }
    fnameData fnames;
    mnameData mnames;
    snameData snames;
    locationData locations;
    public static void main (String args[]) {
        dataGenerator d = new dataGenerator();
        System.out.println(d.toString());
    }
}

