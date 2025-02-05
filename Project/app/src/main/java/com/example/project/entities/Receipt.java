package com.example.project.entities;

import android.graphics.Bitmap;

import com.example.project.DataManager;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Receipt implements Serializable {
    public String id;
    public String id_books;

    public String name;
    public String summary;
    public String name_author;
    public String category;
    public int inventory_quantity;

    public String image;
    public String status;
    public String first_name;
    public String last_name;
    public String gender;
    public String email;
    public String phone;
    public Timestamp date_start;
    public Timestamp date_return;

    public Book[] books;
    public transient Bitmap decodedByte;

    public String getStatus() {
        return status;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Timestamp getDate_start() {
        return date_start;
    }

    public Timestamp getDate_return() {
        return date_return;
    }

    public Book getBookByID(String id){
        Optional<Book> result = Arrays.stream(DataManager.getInstance().getBooks())
                .filter(book -> book.id.equals(id))
                .findFirst();
        return result.orElse(null);
    }

    public Book[] getBooksByIDs(){
        if(books != null) return books;
        String[] IDs = id_books.split(",");
        books = new Book[IDs.length];
        for (int i = 0; i < IDs.length; i++) {
            books[i] = getBookByID(IDs[i]);
        }
        return books;
    }
}
