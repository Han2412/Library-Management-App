package com.example.project.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.DataManager;
import com.example.project.R;
import com.example.project.entities.Book;
import com.example.project.entities.DataResponse;
import com.example.project.entities.Receipt;
import com.example.project.network.SocketEventListener;
import com.example.project.network.WebSocketClient;
import com.example.project.ui.custom_adapter.CustomReceiptAdapter;
import com.example.project.utils.Constants;
import com.example.project.utils.LoadingDialog;
import com.example.project.utils.PopupUtils;
import com.example.project.utils.UIService;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.pdf417.PDF417Writer;
import com.itextpdf.kernel.geom.PageSize;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import android.view.LayoutInflater;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;


public class DetailReceiptActivity extends AppCompatActivity implements  SocketEventListener {

    EditText editDateFrom;
    EditText editDateTo;

    CustomReceiptAdapter  adapter ;

    ListView listView;
    View view;
    Receipt receipt = null;
    private Context FragmentReturned;

    public DetailReceiptActivity() {

    }

    @SuppressLint({"RestrictedApi", "MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIService.HideStatusBar(this, this);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
        setContentView(R.layout.activity_detail_receipt);

        Gson gson = new Gson();

        Intent intent = getIntent();
        String json = intent.getStringExtra("receipt");
        receipt = gson.fromJson(json, Receipt.class);

        ImageButton btnback = findViewById(R.id.btnBack);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button btnCancel = findViewById(R.id.btnCancel);
        Button btn_update = findViewById(R.id.btnUpdate);
        Button btnRemark = findViewById(R.id.btnMarkReturn);

        // Kiểm tra Intent có chứa extra "hideCancel" không
        boolean hideCancel = getIntent().getBooleanExtra("hideCancel", false);
        Log.d("DetailReceiptActivity", "hideCancel: loi " + hideCancel);

        if (hideCancel) {
            // Ẩn btnCancel nếu cần
            btnCancel.setVisibility(View.GONE);
            btn_update.setVisibility(View.GONE);
            btnRemark.setVisibility(View.GONE);
        }

        LinearLayout container = findViewById(R.id.container_items_book);
        LayoutInflater inflater = LayoutInflater.from(this); // Tạo LayoutInflater

        btn_update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    onClickUpdate();
                    Log.e("YourTag", "ParseException occurred");
                } catch (ParseException e) {
                    Log.e("YourTag", "ParseException occurred", e);
                    throw new RuntimeException(e);
                }
            }
        });

        // Kiểm tra xem Intent có dữ liệu không
        if (intent.hasExtra("receipt")) {
            Book[] books = receipt.getBooksByIDs();
            Log.d("Receipt Information", "Receipt Status: " + receipt.getStatus());

            for (int i = 0; i < books.length; i++) { // Số lượng item bạn muốn chèn
                View itemView = inflater.inflate(R.layout.list_item_book_review, null); // Inflate layout của mỗi item
                // Thực hiện các thay đổi cần thiết trên itemView, như đặt dữ liệu, sự kiện, v.v.
                // Ví dụ:
                TextView name_book = itemView.findViewById(R.id.name_book);
                TextView author_name = itemView.findViewById(R.id.author_name);
                TextView summary = itemView.findViewById(R.id.summary);

                name_book.setText(books[i].name);
                author_name.setText(books[i].name_author);
                summary.setText((books[i].summary));

                ImageView imageView = itemView.findViewById(R.id.image_book);
                byte[] decodedString = Base64.decode(books[i].image, Base64.DEFAULT);
                if (decodedString != null && decodedString.length > 0){
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    books[i].decodedByte = decodedByte;
                    imageView.setImageBitmap(decodedByte);
                }

                // Thêm itemView vào LinearLayout
                container.addView(itemView);

                EditText edit_first_name = findViewById(R.id.edit_First_Name);
                edit_first_name.setText(receipt.first_name);

                EditText edit_last_name = findViewById(R.id.edit_Last_Name);
                edit_last_name.setText(receipt.last_name);

                RadioButton maleRadioButton = findViewById(R.id.maleRadioButton);
                maleRadioButton.setChecked(receipt.gender.equals(maleRadioButton.getText()));
                RadioButton femaleRadioButton = findViewById(R.id.femaleRadioButton);
                femaleRadioButton.setChecked(receipt.gender.equals(femaleRadioButton.getText()));
                RadioButton nonBinaryRadioButton = findViewById(R.id.nonBinaryRadioButton);
                nonBinaryRadioButton.setChecked(receipt.gender.equals(nonBinaryRadioButton.getText()));

                TextView edit_email = findViewById(R.id.edit_Contact_Email);
                edit_email.setText(receipt.email);

                TextView edit_region_number = findViewById(R.id.edit_region_number);
                edit_region_number.setText("+" + (!receipt.phone.isEmpty() ? receipt.phone.substring(1, 3) : ""));

                TextView edit_phone = findViewById(R.id.editContactPhone);
                edit_phone.setText(!receipt.phone.isEmpty() ? receipt.phone.substring(3) : "");

                editDateFrom = findViewById(R.id.editDateFrom);
                editDateFrom.setEnabled(false);
                editDateFrom.setBackgroundResource(R.drawable.ip_disable);
                editDateFrom.setText(receipt.date_start.toString());

                editDateTo = findViewById((R.id.editDateTo));
                editDateTo.setText(receipt.date_return.toString());
            }
        }

        if (intent.hasExtra("scannedContent")) {


            Book[] books = receipt.getBooksByIDs();
            Log.d("Receipt Information", "Receipt Status: " + receipt.getStatus());

            for (int i = 0; i < books.length; i++) { // Số lượng item bạn muốn chèn
                View itemView = inflater.inflate(R.layout.list_item_book_review, null); // Inflate layout của mỗi item
                // Thực hiện các thay đổi cần thiết trên itemView, như đặt dữ liệu, sự kiện, v.v.
                // Ví dụ:
                TextView name_book = itemView.findViewById(R.id.name_book);
                TextView author_name = itemView.findViewById(R.id.author_name);
                TextView summary = itemView.findViewById(R.id.summary);

                name_book.setText(books[i].name);
                author_name.setText(books[i].name_author);
                summary.setText((books[i].summary));

                ImageView imageView = itemView.findViewById(R.id.image_book);
                byte[] decodedString = Base64.decode(books[i].image, Base64.DEFAULT);
                if (decodedString != null && decodedString.length > 0){
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    books[i].decodedByte = decodedByte;
                    imageView.setImageBitmap(decodedByte);
                }

                // Thêm itemView vào LinearLayout
                container.addView(itemView);

                EditText edit_first_name = findViewById(R.id.edit_First_Name);
                edit_first_name.setText(receipt.first_name);

                EditText edit_last_name = findViewById(R.id.edit_Last_Name);
                edit_last_name.setText(receipt.last_name);

                RadioButton maleRadioButton = findViewById(R.id.maleRadioButton);
                maleRadioButton.setChecked(receipt.gender.equals(maleRadioButton.getText()));
                RadioButton femaleRadioButton = findViewById(R.id.femaleRadioButton);
                femaleRadioButton.setChecked(receipt.gender.equals(femaleRadioButton.getText()));
                RadioButton nonBinaryRadioButton = findViewById(R.id.nonBinaryRadioButton);
                nonBinaryRadioButton.setChecked(receipt.gender.equals(nonBinaryRadioButton.getText()));

                TextView edit_email = findViewById(R.id.edit_Contact_Email);
                edit_email.setText(receipt.email);

                TextView edit_region_number = findViewById(R.id.edit_region_number);
                edit_region_number.setText("+" + (!receipt.phone.isEmpty() ? receipt.phone.substring(1, 3) : ""));

                TextView edit_phone = findViewById(R.id.editContactPhone);
                edit_phone.setText(!receipt.phone.isEmpty() ? receipt.phone.substring(3) : "");

                editDateFrom = findViewById(R.id.editDateFrom);
                editDateFrom.setEnabled(false);
                editDateFrom.setBackgroundResource(R.drawable.ip_disable);
                editDateFrom.setText(receipt.date_start.toString());

                editDateTo = findViewById((R.id.editDateTo));
                editDateTo.setText(receipt.date_return.toString());
            }
        }

        ImageView btnCalendar = findViewById(R.id.btnDateTo);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        findViewById(R.id.btnMarkReturn).setOnClickListener(view -> {
            // Hiển thị loading dialog để biểu thị rằng yêu cầu đang được xử lý
            LoadingDialog.getInstance(this).show();
            Receipt newReceipt = this.receipt;
            newReceipt.status = "Return";

            // Tạo một đối tượng JSONObject để chứa dữ liệu yêu cầu
            JSONObject remarkObject = new JSONObject();
            try {
                // Thêm các trường dữ liệu vào JSONObject
                remarkObject.put("event", Constants.EVENT_REMARK); // Xác định sự kiện
                remarkObject.put("receipt", gson.toJson(this.receipt));
                remarkObject.put("username", DataManager.getInstance().username);

                // Chuyển đổi JSONObject thành chuỗi JSON
                String message = remarkObject.toString();
                Log.d("Client", "Sending message to server: " + message);
                // Gửi yêu cầu cập nhật lên máy chủ thông qua kết nối WebSocket
                WebSocketClient.getInstance().requestToServer(message,  this);

            } catch (JSONException e) {
                // Xử lý nếu có lỗi khi tạo JSONObject
                throw new RuntimeException(e);
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(view -> {
            // Hiển thị loading dialog để biểu thị rằng yêu cầu đang được xử lý
            LoadingDialog.getInstance(this).show();
            Receipt newReceipt = new Receipt();
            newReceipt.status = "Cancel";
            newReceipt.id = newReceipt.getId();
            this.receipt = newReceipt;
            System.out.println("187  "+ this.receipt.getStatus());

            // Tạo một đối tượng JSONObject để chứa dữ liệu yêu cầu
            JSONObject remarkObject = new JSONObject();
            try {
                // Thêm các trường dữ liệu vào JSONObject

                remarkObject.put("event", Constants.EVENT_REMARK); // Xác định sự kiện
                remarkObject.put("receipt", gson.toJson(newReceipt));
                remarkObject.put("username", DataManager.getInstance().username);

                // Chuyển đổi JSONObject thành chuỗi JSON
                String message = remarkObject.toString();
                Log.d("Client", "Sending message to server: " + message);
                // Gửi yêu cầu cập nhật lên máy chủ thông qua kết nối WebSocket
                WebSocketClient.getInstance().requestToServer(message,  this);
            } catch (JSONException e) {
                // Xử lý nếu có lỗi khi tạo JSONObject
                throw new RuntimeException(e);
            }
        });

        ImageView barCode = findViewById(R.id.barcode);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(receipt.getId(), BarcodeFormat.CODE_128, 400, 200);
            barCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        Button btnPrint = findViewById(R.id.btn_export_pdf);
        btnPrint.setOnClickListener(view -> {
            ImageView viewToExport = findViewById(R.id.barcode);
            exportViewToPdf(viewToExport);
            Toast toast = Toast.makeText(getApplicationContext(), "Print Success Fully To Download Folder", Toast.LENGTH_SHORT);
            toast.show();
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
    public void onLoginResponse(boolean result) throws JSONException {
        if(result){
            GetAllData();
        }
        else{
            LoadingDialog.getInstance(this).hide();
            PopupUtils.showPopup(this, "Login Result Notification", "Login attempt failed. Please check your credentials and try again.", Constants.TYPE_ALERT.OK, null, null);
        }
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
        System.out.println("Detail "+ result);
        if(result ) {
            GetAllData();
            PopupUtils.showPopup(this, "Notify", "The status has been successfully updated.", Constants.TYPE_ALERT.OK, new PopupUtils.OnClickListener() {
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
            PopupUtils.showPopup(this, "AAAAWarning", "The status update failed. Please try again", Constants.TYPE_ALERT.OK, null, null);
            LoadingDialog.getInstance(this).hide();
        }
    }

    public void onClickUpdate() throws ParseException {
        LoadingDialog.getInstance(this).show();
        Gson gson = new Gson();
        Log.e("Update click", "ParseException occurred");
        Intent intent = getIntent();
        String json = intent.getStringExtra("receipt");
        Receipt receipt = gson.fromJson(json, Receipt.class);

        Receipt newReceipt = new Receipt();

        EditText edit_first_name = findViewById(R.id.edit_First_Name);
        newReceipt.first_name = edit_first_name.getText().toString();

        EditText edit_last_name = findViewById(R.id.edit_Last_Name);
        newReceipt.last_name = edit_last_name.getText().toString();

        RadioGroup genderRadioGroup = findViewById(R.id.genderRadioGroup);
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        String selectedGender = selectedRadioButton.getText().toString();
        newReceipt.gender = selectedGender;

        EditText edit_email = findViewById(R.id.edit_Contact_Email);
        newReceipt.email = edit_email.getText().toString();

        EditText edit_phone1 = findViewById(R.id.editContactPhone);

        newReceipt.phone = edit_phone1.getText().toString()  ;

        newReceipt.id = receipt.getId();

        this.receipt = newReceipt;

        JSONObject loginObject = new JSONObject();
        try {
            loginObject.put("event", Constants.EVENT_UPDATE);
            loginObject.put("receipt", gson.toJson(newReceipt));
            loginObject.put("username", DataManager.getInstance().username);
            String mess = loginObject.toString();

            WebSocketClient.getInstance().requestToServer(mess, this);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void onHandleUpdate(boolean result) throws JSONException {
        System.out.println("Update "+ result);

        if(result ) {
            GetAllData();
            PopupUtils.showPopup(this, "Notify", "The loan form has been successfully updatedl.", Constants.TYPE_ALERT.OK, new PopupUtils.OnClickListener() {
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
            PopupUtils.showPopup(this, "Warning", "Update failed. Please try again", Constants.TYPE_ALERT.OK, null, null);
            LoadingDialog.getInstance(this).hide();
        }
    }

    void GetAllData() throws JSONException {
        JSONObject loginObject = new JSONObject();
        loginObject.put("event", Constants.EVENT_GET_DATA);
        loginObject.put("username", DataManager.getInstance().username);
        String mess = loginObject.toString();
        WebSocketClient.getInstance().requestToServer(mess, this);
    }

    public void exportViewToPdf(View view) {
        // Step 1: Create a PdfDocument
        PdfDocument document = new PdfDocument();

        // Step 2: Define Page Info
        int width = 400; // A4 width in points
        int height = 300; // A4 height in points
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();

        // Step 3: Start a Page
        PdfDocument.Page page = document.startPage(pageInfo);

        // Step 4: Draw Layout on Canvas
        Canvas canvas = page.getCanvas();
        view.draw(canvas);

        // Step 5: Finish Page
        document.finishPage(page);

        // Step 6: Write Document to File
        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directoryPath, "layout_view.pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Log.d("PDF", "PDF file generated successfully at " + file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PDF", "Error generating PDF file", e);
        }

        // Close the document
        document.close();
    }
}