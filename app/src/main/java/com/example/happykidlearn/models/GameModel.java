package com.example.happykidlearn.models;

import android.app.Activity;

public class GameModel {

    private String id;
    private String title;
    private String category;
    private String description;
    private int iconResId;
    private int colorResId;
    private Class<? extends Activity> activityClass;

    public GameModel(String id, String title, String category, String description, int iconResId, int colorResId, Class<? extends Activity> activityClass) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.iconResId = iconResId;
        this.colorResId = colorResId;
        this.activityClass = activityClass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public int getColorResId() {
        return colorResId;
    }

    public void setColorResId(int colorResId) {
        this.colorResId = colorResId;
    }

    public Class<? extends Activity> getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class<? extends Activity> activityClass) {
        this.activityClass = activityClass;
    }
}