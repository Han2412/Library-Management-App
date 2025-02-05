package com.example.project.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class DataResponse {
    private Book[] books;
    private Receipt[] receipts;

    public Book[] getBooks() {
        return books;
    }

    public Receipt[] getReceipts() {
        return receipts;
    }
}
