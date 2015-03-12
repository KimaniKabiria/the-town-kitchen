package com.codepath.the_town_kitchen.models;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulina on 3/7/15.
 */

@Table(name = "meal")

public class Meal extends Model {

    @Column(name = "uid", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long uid;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private double price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "date")
    private String date;

    public Long getUid() { return uid; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public double getPrice() { return price; }

    public String getImageUrl() { return imageUrl; }

    public int getQuantity() { return quantity; }

    public String getDate() { return date; }

    public Meal() {
        super();
    }

    public static ArrayList<Meal> fromJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() < 1) return null;
        ArrayList<Meal> meals = new ArrayList<>(jsonArray.length());
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject resultJson = null;
                try {
                    resultJson = jsonArray.getJSONObject(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                Meal meal = Meal.fromJson(resultJson);

                if (meal != null) {
                    meals.add(meal);
                }

            }
            ActiveAndroid.setTransactionSuccessful();
            return meals;
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static Meal fromJson(JSONObject jsonObject) {
        Meal meal = new Meal();
        try {
            Long currentMealId = jsonObject.getLong("uid");
            Meal existingMeal = new Select().from(Meal.class)
                    .where("uid = ?", currentMealId)
                    .executeSingle();
            if (existingMeal != null) {
                meal = existingMeal;
            }
            meal.uid = currentMealId;
            meal.name = jsonObject.has("name") ? jsonObject.getString("name") : "";
            meal.description = jsonObject.has("description") ? jsonObject.getString("description") : "";
            meal.price = jsonObject.has("cost") ? jsonObject.getDouble("cost") : 0.0;
            meal.imageUrl = jsonObject.has("imageUrl") ? jsonObject.getString("imageUrl") : "";
            meal.quantity = jsonObject.has("quantity") ? jsonObject.getInt("quantity") : 0;
            meal.date = jsonObject.has("date") ? jsonObject.getString("date") : "";
            meal.save();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return meal;
    }

    public static ArrayList<Meal> fromCache() {
        ArrayList<Meal> alMeals = new ArrayList<>();
        List<Meal> meals = new Select()
                .from(Meal.class)
                .execute();
        alMeals.addAll(meals);
        return alMeals;
    }

    public static void deleteAll() {
        new Delete().from(Meal.class).execute();
    }
}

