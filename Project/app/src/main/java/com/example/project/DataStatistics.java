package com.example.project;

public class DataStatistics {
    private int image;
    private String nameBook;
    private String numberBorBook;
    private String numberReturnBook;
    private String revenue;
    private String inventory;

    public DataStatistics() {

    }

    public DataStatistics(int image, String nameBook, String numberBorBook, String numberReturnBook, String revenue, String inventory) {
        this.image = image;
        this.nameBook = nameBook;
        this.numberBorBook = numberBorBook;
        this.numberReturnBook = numberReturnBook;
        this.revenue = revenue;
        this.inventory = inventory;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getNameBook() {
        return nameBook;
    }

    public void setNameBook(String nameBook) {
        this.nameBook = nameBook;
    }

    public String getNumberBorBook() {
        return numberBorBook;
    }

    public void setNumberBorBook(String numberBorBook) {
        this.numberBorBook = numberBorBook;
    }

    public String getNumberReturnBook() {
        return numberReturnBook;
    }

    public void setNumberReturnBook(String numberReturnBook) {
        this.numberReturnBook = numberReturnBook;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }
}
