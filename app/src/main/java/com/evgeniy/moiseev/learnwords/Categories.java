package com.evgeniy.moiseev.learnwords;

import java.util.ArrayList;
import java.util.List;

public class Categories {
    private List<Category> categories;

    public Categories() {
        categories = new ArrayList<>();
        categories.add(new Category("all", "все", "всі", R.drawable.ic_all));
        categories.add(new Category("work", "работа", "робота", R.drawable.ic_work));
        categories.add(new Category("adverb", "наречие", "прислівник", R.drawable.ic_adverb));
        categories.add(new Category("travel", "путешествие", "подорож", R.drawable.ic_travel));
        categories.add(new Category("science", "наука", "наука", R.drawable.ic_science));
        categories.add(new Category("underwater world", "водный мир", "водний світ", R.drawable.ic_underwater_world));
        categories.add(new Category("profession", "профессии", "професії", R.drawable.ic_profession));
        categories.add(new Category("verb", "глагол", "дієслово", R.drawable.ic_verb));
        categories.add(new Category("what kind?", "какой?", "який?", R.drawable.ic_what_kind));
        categories.add(new Category("time", "время", "час", R.drawable.ic_time));
        categories.add(new Category("nature", "природа", "природа", R.drawable.ic_nature));
        categories.add(new Category("transport", "транспорт", "транспорт", R.drawable.ic_transport));
        categories.add(new Category("airport", "аэропорт", "аеропорт", R.drawable.ic_airport));
        categories.add(new Category("people", "человек", "людина", R.drawable.ic_people));
        categories.add(new Category("home", "дом", "дім", R.drawable.ic_home));
        categories.add(new Category("countries", "страны", "країни", R.drawable.ic_countries));
        categories.add(new Category("food & drink", "еда и напитки", "їжа та напої", R.drawable.ic_food_and_drink));
        categories.add(new Category("school", "школа", "школа", R.drawable.ic_school));
        categories.add(new Category("health", "здоровье", "здоров'я", R.drawable.ic_health));
        categories.add(new Category("town", "город", "місто", R.drawable.ic_city));
        categories.add(new Category("animal", "животные", "тварини", R.drawable.ic_animal));
        categories.add(new Category("color", "цвет", "колір", R.drawable.ic_color));
        categories.add(new Category("anatomy", "анатомия", "анатомія", R.drawable.ic_anatomy));
        categories.add(new Category("clothes", "одежда", "одяг", R.drawable.ic_clothes));
        categories.add(new Category("vegetation", "растительность", "рослинність", R.drawable.ic_vegetation));
        categories.add(new Category("space", "космос", "космос", R.drawable.ic_space));
        categories.add(new Category("family", "семья", "сім'я", R.drawable.ic_family));
        categories.add(new Category("tool", "инструмент", "інструмент", R.drawable.ic_tool));
        categories.add(new Category("sport", "спорт", "спорт", R.drawable.ic_sport));
        categories.add(new Category("vegetable", "овощи", "овочі", R.drawable.ic_vegetable));
        categories.add(new Category("tableware", "посуда", "посуд", R.drawable.ic_tableware));
        categories.add(new Category("weather", "погода", "погода", R.drawable.ic_weather));
        categories.add(new Category("material", "материал", "матеріал", R.drawable.ic_material));
        categories.add(new Category("stationery", "канцтовары", "канцтовари", R.drawable.ic_stationery));
        categories.add(new Category("hygiene", "гигиена", "гігієна", R.drawable.ic_hygiene));
        categories.add(new Category("textile", "ткань", "тканина", R.drawable.ic_textile));
        categories.add(new Category("geometry", "геометрия", "геометрія", R.drawable.ic_geometry));
        categories.add(new Category("sense", "чувство", "відчуття", R.drawable.ic_sense));
    }

    public List<String> getEngCategories() {
        List<String> engCategories = new ArrayList<>();
        for (Category c : categories) {
            engCategories.add(c.getCategoryEng());
        }
        return engCategories;
    }

    public List<String> getRusCategories() {
        List<String> rusCategories = new ArrayList<>();
        for (Category c : categories) {
            rusCategories.add(c.getCategoryRus());
        }
        return rusCategories;
    }

    public List<String> getUkrCategories() {
        List<String> ukrCategories = new ArrayList<>();
        for (Category c : categories) {
            ukrCategories.add(c.getCategoryUkr());
        }
        return ukrCategories;
    }

    public Category getCategoryEng(String categoryEng) {
        for (Category c : categories) {
            if (c.getCategoryEng().equals(categoryEng)) {
                return c;
            }
        }
        return null;
    }

    public Category getCategoryRus(String categoryRus) {
        for (Category c : categories) {
            if (c.getCategoryRus().equals(categoryRus)) {
                return c;
            }
        }
        return null;
    }

    public Category getCategoryUkr(String categoryUkr) {
        for (Category c : categories) {
            if (c.getCategoryUkr().equals(categoryUkr)) {
                return c;
            }
        }
        return null;
    }

    public List<Category> getCategories() {
        return categories;
    }
}
