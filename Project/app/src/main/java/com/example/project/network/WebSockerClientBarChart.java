package com.example.project.network;

import android.util.Log;

import com.example.project.utils.Constants;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSockerClientBarChart {

    private OnMessageReceivedListenner messageReceivedListenner;
    private WebSocketClient webSocketClient;
    public void connectWebSockert(){
        URI uri;
        try {
            uri = new URI(Constants.SERVER_URL);
        }catch (URISyntaxException e){
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("WebSocket", "Opened");
            }

            @Override
            public void onMessage(String message) {
                Log.i("WebSocket", "Message: " + message);
                try {
                    JSONObject response = new JSONObject(message);
                    if(messageReceivedListenner!=null){
                        messageReceivedListenner.onMessageReceived(message);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("WebSocket", "Closed " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.e("WebSocket", "Error " + ex.getMessage());
            }
        };
        webSocketClient.connect();
    }
    public void putDataEventBarChart(String event,String dayA,String dayB){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event",event);
            jsonObject.put("dayA",dayA);
            jsonObject.put("dayB",dayB);

        }catch (JSONException exception){
            exception.printStackTrace();
        }
        webSocketClient.send(jsonObject.toString());
    }

    public void putDataEventStatisticsDetail(String event,String dayA,String dayB){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event",event);
            jsonObject.put("dayA",dayA);
            jsonObject.put("dayB",dayB);

        }catch (JSONException exception){
            exception.printStackTrace();
        }
        webSocketClient.send(jsonObject.toString());
    }
    public void setOnMessageReceivedListener(OnMessageReceivedListenner listener) {
        this.messageReceivedListenner = listener;
    }

}
