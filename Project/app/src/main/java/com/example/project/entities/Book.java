package com.example.project.entities;

import android.graphics.Bitmap;

import java.io.Serializable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Book implements Serializable {
    public String id;
    public String name;
    public String summary;
    public String name_author;
    public int inventory_quantity;
    public String image;
    public String category;
    public String date_add;
    public transient Bitmap decodedByte;
    public int price;

    public Book() {
    }

    public Book(String id, String name, String summary,
                String name_author, int inventory_quantity, String image, String category, String dateToAdd, int price) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.category = category;
        this.name_author = name_author;
        this.inventory_quantity = inventory_quantity;
        this.image = image;
        this.date_add = dateToAdd;
        this.price = price;
    }

    public String getDate_add() {
        return date_add;
    }

    public int getPrice() {
        return price;
    }

    public void setDate_add(String date_add) {
        this.date_add = date_add;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setName_author(String name_author) {
        this.name_author = name_author;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setInventory_quantity(int inventory_quantity) {
        this.inventory_quantity = inventory_quantity;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public String getName_author() {
        return name_author;
    }

    public int getInventory_quantity() {
        return inventory_quantity;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ", name_author='" + name_author + '\'' +
                ", inventory_quantity=" + inventory_quantity +
                '}';
    }
}
