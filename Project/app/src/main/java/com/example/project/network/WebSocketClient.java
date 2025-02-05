package com.example.project.network;

import android.util.Log;

import com.example.project.utils.Constants;
import com.example.project.utils.ConvertService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.os.Handler;
import android.os.Looper;
import android.view.View;


public class WebSocketClient extends WebSocketListener {

    //region Instance
    private static WebSocketClient _webSocketClient;

    public static synchronized WebSocketClient getInstance() {
        if (_webSocketClient == null) {
            _webSocketClient = new WebSocketClient();
            _webSocketClient.init();
        }
        return _webSocketClient;
    }
    //endregion

    private WebSocket _webSocket;
    private SocketEventListener _listener;
    private WebSocketResponseListener webSocketResponseListener;

    public void init() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // Set to 0 to disable timeout
                .build();

        Request request = new Request.Builder()
                .url(Constants.SERVER_URL)
                .build();

        _webSocket = client.newWebSocket(request, this);
    }

    //region Server Methods
    public void requestToServer(String message, SocketEventListener _listener){
        _webSocket.send(message);
        this._listener = _listener;
    }

    public void send(String message){
        _webSocket.send(message);
    }
    public void send(String message, WebSocketResponseListener listener){
        _webSocket.send(message);
        this.webSocketResponseListener = listener;
    }

    public void handlerEvent(String text) throws JSONException {
        JSONObject data = ConvertService.parseToJsonObject(text);
        String event = (String) data.get("event");
        switch (event){
            case Constants.EVENT_LOGIN:
                _listener.onLoginResponse(Boolean.parseBoolean((String) data.get("result")));
                break;
            case Constants.EVENT_GET_DATA:
                _listener.onGetDataResponse((String) data.get("data"));
                break;
            case Constants.EVENT_ORDER:
                _listener.onOrderResponse(Boolean.parseBoolean((String) data.get("result")));
                break;
            case Constants.EVENT_CHECK_USERNAME:
                webSocketResponseListener.checkUserNameResponse((String) data.get("data"));
                break;
            case Constants.EVENT_ADD_BOOK:
                _listener.onAddBookResponse(Boolean.parseBoolean((String) data.get("result")));
                break;
            case Constants.EVENT_ADD_CATEGORY:
                _listener.onAddCategoryResponse(Boolean.parseBoolean((String) data.get("result")));
                break;
            case Constants.EVENT_GET_CATEGORIES:
                _listener.onGetCategoryResponse((String) data.get("data"));
                break;
            case Constants.EVENT_REMARK:
                _listener.onHandlePhieu(Boolean.parseBoolean((String) data.get("result")));
                Log.d("TAGRemark", "Evencancel: " +(String) data.get("result"));
                break;
            case Constants.EVENT_UPDATE:
                _listener. onHandleUpdate(Boolean.parseBoolean((String) data.get("result")));
                Log.d("Update", "Event Update: " +(String) data.get("result"));
                break;
            default:
                break;
        }
    }
    //endregion

    private void runOnUiThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    //region Override Methods
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        Log.d("WebSocket", "Received message: " + text);
        // Handle received text message
        // Handle received text message on the main thread
        runOnUiThread(() -> {
            try {
                handlerEvent(text);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        // Handle received binary message
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        // WebSocket is about to close
        System.out.println("WebSocket is about to close");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        // WebSocket closed
        System.out.println("WebSocket closed");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        // WebSocket failure
        System.out.println("WebSocket failure: " + t.getMessage());
        t.printStackTrace();

        if (response != null) {
            System.out.println("Response: " + response.toString());
        }
    }



    //endregion
}
