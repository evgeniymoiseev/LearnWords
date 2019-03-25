package com.evgeniy.moiseev.learnwords;

public class Category {
    private String categoryEng;
    private String categoryRus;
    private String categoryUkr;
    private int drawableResId;

    public Category(String categoryEng, String categoryRus, String categoryUkr, int drawableResId) {
        this.categoryEng = categoryEng;
        this.categoryRus = categoryRus;
        this.categoryUkr = categoryUkr;
        this.drawableResId = drawableResId;
    }

    public int getDrawableResId() {
        return drawableResId;
    }

    public String getCategoryEng() {
        return categoryEng;
    }

    public String getCategoryRus() {
        return categoryRus;
    }

    public String getCategoryUkr() {
        return categoryUkr;
    }
}
