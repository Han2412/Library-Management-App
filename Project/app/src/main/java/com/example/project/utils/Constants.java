package com.example.project.utils;

public class Constants {
<<<<<<< HEAD
    // main ip
    public static final String SERVER_URL = "ws://192.168.1.3:3500"; // Replace with your server IP or hostname
=======
    // main ipc final String SERVER_URL = "ws://192.168.1.11:3500"; // Replace with your server IP or hostname
    public static final String SERVER_URL = "ws://172.20.10.4:3500"; // Replace with your server IP or hostname
>>>>>>> a0c7bce074c1be559e595582893b61d5fcfd61d6

    // events handlers
    public static final String EVENT_LOGIN = "login";a
    public static final String EVENT_GET_DATA = "getData";
    public static final String EVENT_ORDER = "order";
    public static final String EVENT_ADD_BOOK = "addBook";
    public static final String EVENT_ADD_CATEGORY = "addCategory";
    public static final String EVENT_GET_CATEGORIES = "getCategory";
    public static final String EVENT_ADD_USER = "addUser";
    public static final String EVENT_CHECK_USERNAME = "checkUsernameExists";
    public static final String EVENT_CHANGE_PASSWORD = "changePassword";
    public static final String EVENT_REMARK = "remark";
    public static final String EVENT_UPDATE = "update";
    // log type tag
    public static final String TAG_WEBSOCKET = "Websocket";

    public enum TYPE_ALERT{
        OK, OK_CANCEL, TRYAGAIN_CANCEL
    }

    public enum MODE_SUB_TABS{
        HOME,
        NOTE,
    }
}

