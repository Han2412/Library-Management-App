package com.example.project.utils;

import org.json.JSONObject;

public class ConvertService {
    public static JSONObject parseToJsonObject(String text) {
        JSONObject jsonObject = null;
        try {
            // Parse JSON to JSONObject
            jsonObject = new JSONObject(text);
        } catch (Exception e) {
            // Log or handle the exception appropriately
            e.printStackTrace();
            // or throw a custom exception
            throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
        }
        return jsonObject;
    }
}
