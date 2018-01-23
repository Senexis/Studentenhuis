package com.timov.studentenhuis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.timov.studentenhuis.dao.MealAdapter;
import com.timov.studentenhuis.dao.MealDAO;
import com.timov.studentenhuis.model.Meal;

import java.util.List;
import java.util.Objects;

public class MainActivity extends ListActivity {
    private static final int REQUEST_CREATE = 0;
    private static final int REQUEST_EDIT = 1;
    private static final int REQUEST_LOGIN = 2;

    private MealDAO mealDAO;
    List<Meal> meals;
    MealAdapter mealAdapter;

    boolean blnShort = false;
    int position = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onBoot();
    }

    public void onBoot() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString("loginToken", null);

        if (token != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Inloggen...");
            progressDialog.show();

            Ion.with(this)
                    .load("http://studentenhuis-api.herokuapp.com/student")
                    .addHeader("authentication", token)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e == null) {
                                String status = result.get("status").getAsString();

                                if (Objects.equals(status, "failed")) {
                                    Intent it = new Intent(getBaseContext(), LoginActivity.class);
                                    startActivityForResult(it, REQUEST_LOGIN);
                                } else {
                                    mealDAO = new MealDAO(getBaseContext());
                                    mealDAO.open();
                                    meals = mealDAO.readAll();

                                    mealAdapter = new MealAdapter(getBaseContext(), meals);
                                    setListAdapter(mealAdapter);

                                    registerForContextMenu(getListView());

                                    mealAdapter.notifyDataSetChanged();
                                }
                            } else {
                                toast("Er is iets misgegaan en de data kon niet gesynchroniseerd worden met de server. Probeer het later opnieuw.\n\n"+e.getMessage());
                                Intent it = new Intent(getBaseContext(), LoginActivity.class);
                                startActivityForResult(it, REQUEST_LOGIN);
                            }
                            progressDialog.dismiss();
                        }
                    });
        } else {
            Intent it = new Intent(getBaseContext(), LoginActivity.class);
            startActivityForResult(it, REQUEST_LOGIN);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                AddMeal();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Meal meal;

        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CREATE) {
                    meal = (Meal) data.getExtras().getSerializable("meal");

                    if (!meal.getName().equals("")) {
                        mealDAO.open();
                        mealDAO.create(meal);

                        meals.add(meal);

                        mealAdapter.notifyDataSetChanged();
                    }
                } else if (requestCode == REQUEST_EDIT) {
                    meal = (Meal) data.getExtras().getSerializable("meal");

                    mealDAO.open();
                    mealDAO.update(meal);

                    meals.set(position, meal);

                    mealAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_LOGIN) {
                    onBoot();
                }
            }
        } catch (Exception e) {
            trace("Error: " + e.getMessage());
        }
    }

    private void AddMeal() {
        try {
            Intent it = new Intent(this, MealActivity.class);
            it.putExtra("type", REQUEST_CREATE);
            startActivityForResult(it, REQUEST_CREATE);
        } catch (Exception e) {
            trace("Error: " + e.getMessage());
        }

    }

    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void trace(String msg) {
        toast(msg);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            if (!blnShort) {
                position = info.position;
            }
            blnShort = false;

            menu.setHeaderTitle("Selecteer een actie");
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        } catch (Exception e) {
            trace("Error: " + e.getMessage());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final Meal meal;
        try {
            int menuItemIndex = item.getItemId();
            meal = (Meal) getListAdapter().getItem(position);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            final String token = preferences.getString("loginToken", null);
            final String userId = preferences.getString("userId", null);

            if (menuItemIndex == 0) {
                Intent it = new Intent(this, MealActivity.class);
                it.putExtra("type", REQUEST_EDIT);
                it.putExtra("meal", meal);
                startActivityForResult(it, REQUEST_EDIT);
            } else if (menuItemIndex == 1) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Hoeveel mee-eters?");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                input.setPadding(32, 32, 32, 32);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
                        JsonElement jsonElement = gson.toJsonTree(meal);
                        JsonObject jsonObject = (JsonObject) jsonElement;

                        JsonObject json = new JsonObject();
                        json.addProperty("idStudent", userId);
                        json.addProperty("aantalMeeEters", input.getText().toString());

                        Ion.with(getBaseContext())
                                .load("POST", "http://studentenhuis-api.herokuapp.com/maaltijd/"+meal.getId()+"/add-student")
                                .addHeader("authentication", token)
                                .setJsonObjectBody(jsonObject)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        if (e == null) {
                                            String status = result.get("status").getAsString();

                                            if (Objects.equals(status, "failed")) {
                                                toast("Er is iets verkeerd gegaan bij het inschrijvan.");
                                            } else {
                                                toast("U bent met succes ingeschreven!");
                                            }
                                        } else {
                                            toast("Er is iets misgegaan en u kon niet worden ingeschreven. Probeer het later opnieuw.\n\n"+e.getMessage());
                                        }
                                    }
                                });
                    }
                });
                alert.setNegativeButton("Annuleren", null);
                alert.show();
            } else if (menuItemIndex == 2) {
                new AlertDialog.Builder(this)
                    .setMessage("Weet je zeker dat je deze maaltijd wilt verwijderen?")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mealDAO.delete(meal);
                            meals.remove(meal);
                            mealAdapter.notifyDataSetChanged();
                        }})
                    .setNegativeButton("Nee", null)
                    .show();
            }
        } catch (Exception e) {
            trace("Error: " + e.getMessage());
        }
        return true;

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        this.position = position;
        blnShort = true;
        this.openContextMenu(l);
    }

    public void onClickLogout(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("loginToken", null);
        editor.apply();

        onBoot();
    }
}