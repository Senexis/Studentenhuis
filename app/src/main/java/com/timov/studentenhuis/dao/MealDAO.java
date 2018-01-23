package com.timov.studentenhuis.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.SqlDateTypeAdapter;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.timov.studentenhuis.model.Meal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MealDAO {
    private String[] columns = {};
    private Context context;

    public MealDAO(Context context) {
        this.context = context;
    }

    public void open() {

    }

    public void close() {

    }

    public long create(Meal meal) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        String token = preferences.getString("loginToken", null);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        JsonElement jsonElement = gson.toJsonTree(meal);
        JsonObject jsonObject = (JsonObject) jsonElement;

        Ion.with(this.context)
                .load("POST", "http://studentenhuis-api.herokuapp.com/maaltijd")
                .addHeader("authentication", token)
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(context, "Er is iets misgegaan en de data kon niet gesynchroniseerd worden met de server. Probeer het later opnieuw.\n\n"+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return meal.getId();
    }

    public Meal read(long id) {
        return new Meal();
    }

    public List<Meal> readAll() {
        List<Meal> results = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        String token = preferences.getString("loginToken", null);

        SqlDateTypeAdapter sqlAdapter = new SqlDateTypeAdapter();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(java.sql.Date.class, sqlAdapter )
                .setDateFormat("yyyy-MM-dd")
                .create();

        try {
            JsonObject json = Ion.with(this.context).load("http://studentenhuis-api.herokuapp.com/maaltijd").addHeader("authentication", token).asJsonObject().get();
            JsonArray array = json.getAsJsonArray("result");

            for (Iterator<JsonElement> i = array.iterator(); i.hasNext();) {
                JsonElement item = i.next();
                Meal meal = gson.fromJson(item, Meal.class);
                results.add(meal);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Er is iets misgegaan en de data kon niet gesynchroniseerd worden met de server. Probeer het later opnieuw.\n\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return results;
    }

    public int update(Meal meal) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        String token = preferences.getString("loginToken", null);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        JsonElement jsonElement = gson.toJsonTree(meal);
        JsonObject jsonObject = (JsonObject) jsonElement;

        Ion.with(this.context)
            .load("PUT", "http://studentenhuis-api.herokuapp.com/maaltijd/" + meal.getId())
            .addHeader("authentication", token)
            .setJsonObjectBody(jsonObject)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if (e != null) {
                        Toast.makeText(context, "Er is iets misgegaan en de data kon niet gesynchroniseerd worden met de server. Probeer het later opnieuw.\n\n"+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        return 1;
    }

    public void delete(Meal meal) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        String token = preferences.getString("loginToken", null);

        Ion.with(this.context)
                .load("DELETE", "http://studentenhuis-api.herokuapp.com/maaltijd/" + meal.getId())
                .addHeader("authentication", token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Toast.makeText(context, "Er is iets misgegaan en de data kon niet gesynchroniseerd worden met de server. Probeer het later opnieuw.\n\n"+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
