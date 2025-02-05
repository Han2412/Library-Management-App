package com.example.project.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.DataManager;
import com.example.project.R;
import com.example.project.entities.Book;
import com.example.project.entities.DataResponse;
import com.example.project.entities.Receipt;
import com.example.project.network.SocketEventListener;
import com.example.project.network.WebSocketClient;
import com.example.project.ui.custom_adapter.CustomBookAdapter;
import com.example.project.ui.custom_adapter.CustomDetailOrderAdapter;
import com.example.project.ui.subFragments.FragmentAll;
import com.example.project.ui.subFragments.FragmentReturned;
import com.example.project.utils.Constants;
import com.example.project.utils.CreateService;
import com.example.project.utils.LoadingDialog;
import com.example.project.utils.PopupUtils;
import com.example.project.utils.UIService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DetailOrderActivity extends AppCompatActivity implements SocketEventListener {

    ListView list_book_review;
    ScrollView borrower_info;
    LinearLayout time_info;
    ListView list_item_book;
    CustomDetailOrderAdapter adapter;
    Button btn_next;
    RelativeLayout region_next;
    RelativeLayout region_submit;
    ImageButton btn_back;
    ImageView frame_order_book;
    ImageView frame_order_user;
    ImageView frame_order_calendar;
    ImageView frame_order_success;
    EditText editDateTo;
    Receipt receipt = null;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIService.HideStatusBar(this, this);
        setContentView(R.layout.activity_detail_order);

        list_book_review = findViewById(R.id.list_book_review);
        borrower_info = findViewById(R.id.borrower_info);
        time_info = findViewById(R.id.time_info);
        list_item_book = findViewById(R.id.list_item_book);

        region_next = findViewById(R.id.region_next);
        region_submit = findViewById(R.id.region_submit);

        adapter = new CustomDetailOrderAdapter(this, R.layout.list_item_book_review);

        // Assuming DataManager.getInstance().getBooks() returns an array of Book objects
        Book[] books = DataManager.getInstance().getBooksSelect().toArray(new Book[0]);
        adapter.addAll(books); // Pass the array of Book objects to the adapter

        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNext1();
            }
        });

        btn_back = findViewById(R.id.btnBack);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack0();
            }
        });

        list_book_review.setAdapter(adapter);

        frame_order_book = findViewById(R.id.frame_order_book);
        frame_order_user = findViewById(R.id.frame_order_user);
        frame_order_calendar = findViewById(R.id.frame_order_calendar);
        frame_order_success = findViewById(R.id.frame_order_success);

        ImageView btnDateTo = findViewById(R.id.btnDateTo);
        btnDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        editDateTo = findViewById(R.id.editDateTo);

        frame_order_book.setBackground(getResources().getDrawable(R.drawable.frame_icon_order0));

        // Get Price
        List<Book> booksSelect = DataManager.getInstance().getBooksSelect();
        int totalPrice = booksSelect.stream()
                .mapToInt(Book::getPrice)
                .sum();

        TextView txtPrice = findViewById(R.id.txt_price);
        txtPrice.setText(totalPrice + "$");

        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onClickSubmitOrder();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        EditText editPhone = findViewById(R.id.edit_phone1);
        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+")) {
                    editPhone.setText("+" + s.toString());
                    editPhone.setSelection(editPhone.getText().length());
                }
            }
        });
    }

    public void showDatePickerDialog(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String formattedDate = String.format("%04d/%02d/%02d", year, monthOfYear + 1, dayOfMonth);
                        editDateTo.setText(formattedDate);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Thêm mã xử lý nếu cần thiết
    }

    public void onClickNext1(){
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNext2();
            }
        });
        list_book_review.setVisibility(View.GONE);
        borrower_info.setVisibility(View.VISIBLE);
        frame_order_book.setBackground(getResources().getDrawable(R.drawable.frame_icon_order1));
        frame_order_user.setBackground(getResources().getDrawable(R.drawable.frame_icon_order0));

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack1();
            }
        });
    }

    public void onClickNext2(){
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNext3();
            }
        });

        borrower_info.setVisibility(View.GONE);
        time_info.setVisibility(View.VISIBLE);
        frame_order_user.setBackground(getResources().getDrawable(R.drawable.frame_icon_order1));
        frame_order_calendar.setBackground(getResources().getDrawable(R.drawable.frame_icon_order0));

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack2();
            }
        });
    }

    public void onClickNext3(){
        region_next.setVisibility(View.GONE);
        region_submit.setVisibility(View.VISIBLE);
        time_info.setVisibility(View.GONE);
        list_item_book.setVisibility(View.VISIBLE);
        frame_order_calendar.setBackground(getResources().getDrawable(R.drawable.frame_icon_order1));
        frame_order_success.setBackground(getResources().getDrawable(R.drawable.frame_icon_order0));
        findViewById(R.id.frame_order).setBackground(getResources().getDrawable(R.drawable.frame_horizontal1));
        // Get Price
        List<Book> booksSelect = DataManager.getInstance().getBooksSelect();
        int totalPrice = booksSelect.stream()
                .mapToInt(Book::getPrice)
                .sum();
        Button btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setText("Confirm Pay " + totalPrice + "$");


        ListView listView = findViewById(R.id.list_item_book);
        CustomBookAdapter adapter = new CustomBookAdapter(this, R.layout.list_item_book, false);
        Book[] books = DataManager.getInstance().getBooksSelect().toArray(new Book[0]);
        adapter.addAll(books); // Pass the array of Book objects to the adapter

        listView.setAdapter(adapter);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack3();
            }
        });
    }

    public void onClickBack0(){
        onBackPressed();
    }

    public void onClickBack1(){
        list_book_review.setVisibility(View.VISIBLE);
        borrower_info.setVisibility(View.GONE);
        frame_order_book.setBackground(getResources().getDrawable(R.drawable.frame_icon_order0));
        frame_order_user.setBackground(getResources().getDrawable(R.drawable.frame_icon_order));

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack0();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNext1();
            }
        });
    }

    public void onClickBack2(){
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrower_info.setVisibility(View.VISIBLE);
                time_info.setVisibility(View.GONE);
                frame_order_user.setBackground(getResources().getDrawable(R.drawable.frame_icon_order0));
                frame_order_calendar.setBackground(getResources().getDrawable(R.drawable.frame_icon_order));

                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickBack1();
                    }
                });

                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickNext2();
                    }
                });
            }
        });
    }

    public void onClickBack3(){
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                region_next.setVisibility(View.VISIBLE);
                region_submit.setVisibility(View.GONE);
                time_info.setVisibility(View.VISIBLE);
                list_item_book.setVisibility(View.GONE);
                frame_order_calendar.setBackground(getResources().getDrawable(R.drawable.frame_icon_order0));
                frame_order_success.setBackground(getResources().getDrawable(R.drawable.frame_icon_order));
                findViewById(R.id.frame_order).setBackground(getResources().getDrawable(R.drawable.frame_horizontal));
                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickBack2();
                    }
                });

                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickNext3();
                    }
                });

            }
        });
    }


    public void onClickSubmitOrder() throws ParseException {
        LoadingDialog.getInstance(this).show();

        Gson gson = new Gson();

        Receipt receipt = new Receipt();
        receipt.id = CreateService.randomID(10);
        receipt.id_books = CreateService.getIDBooksFromBooks(DataManager.getInstance().getBooksSelect().toArray(new Book[0]));
        receipt.status = "Borrowing";

        EditText edit_first_name = findViewById(R.id.edit_first_name);
        receipt.first_name = edit_first_name.getText().toString();

        EditText edit_last_name = findViewById(R.id.edit_last_name);
        receipt.last_name = edit_last_name.getText().toString();

        RadioGroup genderRadioGroup = findViewById(R.id.genderRadioGroup);
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        String selectedGender = selectedRadioButton.getText().toString();
        receipt.gender = selectedGender;

        EditText edit_email = findViewById(R.id.edit_email);
        receipt.email = edit_email.getText().toString();

        EditText edit_phone1 = findViewById(R.id.edit_phone1);
        EditText edit_phone2 = findViewById(R.id.edit_phone2);
        receipt.phone = edit_phone1.getText().toString() + edit_phone2.getText().toString();

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        receipt.date_start =  Timestamp.valueOf(formattedDateTime);

        EditText edit_date_from = findViewById(R.id.editDateTo);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        // Chuyển đổi chuỗi thành đối tượng Date
        Date parsedDate = dateFormat.parse(edit_date_from.getText().toString());
        // Tạo một đối tượng Timestamp từ đối tượng Date, với giờ, phút, giây mặc định là 00:00:00
        receipt.date_return = new Timestamp(parsedDate.getTime());

        this.receipt = receipt;

        JSONObject loginObject = new JSONObject();
        try {
            loginObject.put("event", Constants.EVENT_ORDER);
            loginObject.put("receipt", gson.toJson(receipt));
            loginObject.put("username", DataManager.getInstance().username);
            String mess = loginObject.toString();

            WebSocketClient.getInstance().requestToServer(mess, this);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }




    @Override
    public void onLoginResponse(boolean result) throws JSONException {

    }

    @Override
    public void onGetDataResponse(String data) {
        Gson gson = new Gson();
        DataResponse dataResponse = gson.fromJson(data, DataResponse.class);
        DataManager.getInstance().UpdateData(dataResponse);
        LoadingDialog.getInstance(this).hide();
    }

    @Override
    public void onOrderResponse(boolean result) throws JSONException {
        System.out.println("Order"+ result);
        if(result) {
            GetAllData();
            DataManager.getInstance().booksSelect.clear();
            PopupUtils.showPopup(this, "Notify", "You have successfully booked the book.", Constants.TYPE_ALERT.OK, new PopupUtils.OnClickListener() {
                @Override
                public void OnPositiveClicked() {
                    finish();
                }

                @Override
                public void OnNegativeClicked() {

                }
            }, null);
        }
        else {
            PopupUtils.showPopup(this, "Warning", "Order book failed. Please try again", Constants.TYPE_ALERT.OK, null, null);
            LoadingDialog.getInstance(this).hide();
        }
    }

    @Override
    public void onAddBookResponse(boolean result) throws JSONException {

    }

    @Override
    public void onAddCategoryResponse(boolean result) throws JSONException {

    }

    @Override
    public void onGetCategoryResponse(String data) throws JSONException {

    }

    public void onHandlePhieu(boolean result) throws JSONException {

    }

    @Override
    public void onHandleUpdate(boolean result) throws JSONException {

    }

    void GetAllData() throws JSONException {
        JSONObject loginObject = new JSONObject();
        loginObject.put("event", Constants.EVENT_GET_DATA);
        loginObject.put("username", DataManager.getInstance().username);
        String mess = loginObject.toString();
        WebSocketClient.getInstance().requestToServer(mess, this);
    }
}
