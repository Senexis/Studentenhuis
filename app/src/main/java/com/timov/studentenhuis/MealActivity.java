package com.timov.studentenhuis;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.timov.studentenhuis.model.Meal;

import java.io.InputStream;
import java.sql.Timestamp;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MealActivity extends Activity {
    private static final int REQUEST_CREATE = 0;
    // private static final int REQUEST_EDIT = 1;

    Meal meal;
    long cookId;

    @Bind(R.id.et_meal_cook) EditText _etCook;
    @Bind(R.id.et_meal_name) EditText _etName;
    @Bind(R.id.et_meal_description) EditText _etDescription;
    @Bind(R.id.et_meal_max_participants) EditText _etMaxParticipants;
    @Bind(R.id.et_meal_price) EditText _etPrice;
    @Bind(R.id.et_meal_start_date) EditText _etStartTimestamp;
    @Bind(R.id.im_meal_image) ImageView _imImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        ButterKnife.bind(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = preferences.getString("userId", null);

        cookId = Long.parseLong(userId);

        final Bundle data = (Bundle) getIntent().getExtras();
        int lint = data.getInt("type");
        if (lint == REQUEST_CREATE) {
            meal = new Meal();
        } else {
            meal = (Meal) data.getSerializable("meal");
        }

        assert meal != null;

        _etCook.setText(meal.getCookName());
        _etName.setText(meal.getName());
        _etDescription.setText(meal.getDescription());
        _etMaxParticipants.setText(String.valueOf(meal.getMaxParticipants()));
        _etPrice.setText(meal.getPrice());

        if (meal.getStartTimestamp() == null)
            meal.setStartTimestamp(new Timestamp(System.currentTimeMillis()));

        _etStartTimestamp.setText(String.valueOf(meal.getStartTimestamp()));

        if (!TextUtils.isEmpty(meal.getImage()))
            new DownloadImageTask(_imImage).execute(meal.getImage());
    }

    public void btnSave_click(View view) {
        try {
            Intent data = new Intent();

            toast(cookId + "");

            meal.setCookId(cookId);
            meal.setName(_etName.getText().toString());
            meal.setDescription(_etDescription.getText().toString());
            meal.setMaxParticipants(Integer.parseInt(_etMaxParticipants.getText().toString()));
            meal.setPrice(_etPrice.getText().toString());
            meal.setStartTimestamp(Timestamp.valueOf(_etStartTimestamp.getText().toString()));

            data.putExtra("meal", meal);
            System.out.println(meal);
            setResult(Activity.RESULT_OK, data);
            finish();
        } catch (Exception e) {
            trace("Error: " + e.getMessage());
        }
    }

    public void btnCancel_click(View view) {
        try {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } catch (Exception e) {
            trace("Error: " + e.getMessage());
        }
    }

    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void trace(String msg) {
        toast(msg);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                //
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
