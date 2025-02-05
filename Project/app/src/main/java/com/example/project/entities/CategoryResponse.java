package com.example.project.entities;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CategoryResponse {
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }
}
