package com.example.project.network;

import org.json.JSONException;
import org.json.JSONObject;

public interface SocketEventListener {
    void onLoginResponse(boolean result) throws JSONException;
    void onGetDataResponse(String data);
    void onOrderResponse(boolean result) throws JSONException;
    void onAddBookResponse(boolean result) throws JSONException;
    void onAddCategoryResponse(boolean result) throws JSONException;
    void onGetCategoryResponse(String data) throws JSONException;
    void onHandlePhieu(boolean result) throws JSONException;
    void onHandleUpdate(boolean result) throws JSONException;
}

