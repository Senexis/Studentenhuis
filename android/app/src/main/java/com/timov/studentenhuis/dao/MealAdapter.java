package com.timov.studentenhuis.dao;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.timov.studentenhuis.R;
import com.timov.studentenhuis.model.Meal;

import java.util.List;

public class MealAdapter extends BaseAdapter {
    private Context context;

    private List<Meal> meals;
    private LayoutInflater inflater;

    public MealAdapter(Context context, List<Meal> meals) {
        super();
        this.context = context;
        this.meals = meals;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void notifyDataSetChanged() {
        try {
            super.notifyDataSetChanged();
        } catch (Exception e) {
            trace("Error: " + e.getMessage());
        }
    }

    private void trace(String msg) {
        toast(msg);
    }

    public void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public int getCount() {
        return meals.size();
    }

    public void remove(final Meal meal) {
        this.meals.remove(meal);
    }

    public void add(final Meal meal) {
        this.meals.add(meal);
    }

    public Object getItem(int position) {
        return meals.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup2) {
        try {
            Meal meal = meals.get(position);

            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.meal_row, null);

                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_meal_name);
                holder.tvDescription = (TextView) convertView.findViewById(R.id.tv_meal_description);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvName.setText(meal.getName());

            if (!TextUtils.isEmpty(meal.getDescription())) {
                holder.tvDescription.setText(meal.getDescription());
            } else {
                holder.tvDescription.setText("Deze maaltijd heeft geen beschrijving.");
            }

            return convertView;

        } catch (Exception e) {
            trace("Error: " + e.getMessage());
        }
        return convertView;
    }

    static class ViewHolder {
        public TextView tvName;
        public TextView tvDescription;
    }
}
