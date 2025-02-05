package com.example.project.ui;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.fonts.Font;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.DataStatistics;
import com.example.project.R;
import com.example.project.network.OnMessageReceivedListenner;
import com.example.project.network.WebSockerClientBarChart;
import com.example.project.ui.custom_adapter.StatisticsAdapter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;

public class StatisticsDetail extends AppCompatActivity {

    WebSockerClientBarChart webSockerClientBarChart;

    EditText edtDayA,edtDayB,searchEditText;

    Button btnStatistic ,searchButton,btnExportPDF ;


    ArrayList<DataStatistics> dataStatistics = new ArrayList<>();;
    ListView listView;

    StatisticsAdapter statisticsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_detail);

        webSockerClientBarChart = new WebSockerClientBarChart();
        webSockerClientBarChart.connectWebSockert();
        setControl();
        setEvent();




    }

    private void setEvent() {

        btnStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dayA = edtDayA.getText().toString();
                String dayB = edtDayB.getText().toString();
                String event = "SstDetail";
                webSockerClientBarChart.putDataEventStatisticsDetail(event,dayA,dayB);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchEditText.getText().toString();
                performSearch(keyword);
            }
        });

        searchEditText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String keyword = searchEditText.getText().toString();
                    performSearch(keyword);
                    return true;
                }
                return false;
            }
        });

        btnExportPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportListViewToPDF();
            }
        });

    }

    private void setControl() {
        listView = findViewById(R.id.lsvStatisticsDetail);
        edtDayA = findViewById(R.id.editTextDateA);
        edtDayB=findViewById(R.id.editTextDateB);
        btnStatistic = findViewById(R.id.buttonStatisticDetail);
        searchButton = findViewById(R.id.searchButton);
        searchEditText = findViewById(R.id.searchEditText);
        btnExportPDF =findViewById(R.id.btnExportPDF);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webSockerClientBarChart.setOnMessageReceivedListener(new OnMessageReceivedListenner() {
            @Override
            public void onMessageReceived(String message) {
                try {
                    JSONObject response = new JSONObject(message);
                    JSONArray dataStatisticArray = response.getJSONArray("dataBooks");
                    dataStatistics.clear();

                    for (int i = 0; i < dataStatisticArray.length(); i++) {
                        JSONObject bookObject = dataStatisticArray.getJSONObject(i);
                        String tensach = bookObject.getString("tensach");
                        int img = 0;
                        switch (tensach){
                            case "Chúng ta đã mỉm cười ":
                                img = R.drawable.chugtadamincuoi;
                                break;
                            case "Khởi hành ":
                                img = R.drawable.kh;
                                break;

                            case "Mọi thứ đều có thể thay đổi ":
                                img = R.drawable.mtdtd;
                                break;
                            case "Làm chủ các mẫu thiết kế kinh điển trong lập trình ":
                                img = R.drawable.tl;
                                break;

                            case "Mem ":
                                img = R.drawable.mem;
                                break;

                            case "Lunar":
                                img = R.drawable.luna;
                                break;
                            case "SÁCHE":
                                img = R.drawable.sache;
                                break;
                            case "SÁCHF":
                                img = R.drawable.sachf;
                                break;
                            case "SÁCHG":
                                img = R.drawable.sachg;
                                break;
                            case "SÁCHK":
                                img = R.drawable.sachh;
                                break;
                            default:
                                Log.e("Err", "onMessageReceived: ",null );

                        }

                        String tongluotmuon = bookObject.getString("tongluotmuon");
                        String tongluottra = bookObject.getString("tongluottra");
                        String doahthu = bookObject.getString("doanhthu");
                        String tonkho = bookObject.getString("tonkho");
                        dataStatistics.add(new DataStatistics(img,tensach,"SumNumberBorBook: "+tongluotmuon,"SumNumberReturnBook: "+tongluottra,"Revenue: "+doahthu,"Inventory: "+tonkho));


                        Log.i("NHAN222", "Tên sách: " + tensach + ", Số lượt mượn: " + tongluotmuon+"\n"+
                                        "so luot tra"+tongluottra+"doanh thu" +doahthu
                                );
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setDataStatistic(dataStatistics);
                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setDataStatistic(ArrayList<DataStatistics> data){
        statisticsAdapter = new StatisticsAdapter(StatisticsDetail.this,R.layout.layoutadapterstatistics,data);
        listView.setAdapter(statisticsAdapter);
        statisticsAdapter.notifyDataSetChanged();
    }

    private void performSearch(String keyword) {
        ArrayList<DataStatistics> searchResults = new ArrayList<>();
        for (DataStatistics data : dataStatistics) {
            if (data.getNameBook().toLowerCase().contains(keyword.toLowerCase())) {
                searchResults.add(data);
            }
        }

        // Cập nhật ListView để hiển thị kết quả tìm kiếm
        updateListView(searchResults);
    }

    private void updateListView(ArrayList<DataStatistics> searchResults) {
        // Cập nhật dữ liệu cho Adapter và ListView
        statisticsAdapter = new StatisticsAdapter(this, R.layout.layoutadapterstatistics, searchResults);
        listView.setAdapter(statisticsAdapter);
        statisticsAdapter.notifyDataSetChanged();
    }

    public void showDatePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final EditText selectedEditText = (EditText) view;
        DatePickerDialog datePickerDialog = new DatePickerDialog(StatisticsDetail.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Lấy ngày được chọn và hiển thị trên ô nhập ngày
                        String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        selectedEditText.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void exportListViewToPDF() {
        try {
            String fpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File file = new File(fpath,"myPDF.pdf");

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Get ListView adapter
            ListAdapter adapter = listView.getAdapter();

            // Add ListView data to PDF
            for (int i = 0; i < adapter.getCount(); i++) {
                View listItemView = adapter.getView(i, null, listView);
                document.add(getViewAsListItem(listItemView));
            }

            document.close();
            Toast.makeText(this, "PDF created successfully", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ListItem getViewAsListItem(View view) throws IOException {
        // Convert view to ListItem
        ListItem listItem = new ListItem();

        // Get data from TextViews
        String name = ((TextView) view.findViewById(R.id.nameBook)).getText().toString();
        String numberBorBook = ((TextView) view.findViewById(R.id.numberBorBook)).getText().toString();
        String numberReturnBook = ((TextView) view.findViewById(R.id.numberReturnBook)).getText().toString();
        String revenue = ((TextView) view.findViewById(R.id.revenue)).getText().toString();
        String inventory = ((TextView) view.findViewById(R.id.inventory)).getText().toString();

        // Get image from ImageView and add it to ListItem
        ImageView imageView = view.findViewById(R.id.imgViewStatistic);
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        listItem.add(new Image(ImageDataFactory.create(byteArray)));

        Log.i("utf8",name +" "+numberBorBook+" "+numberReturnBook);
        // Add data to ListItem
        // Add data to ListItem with UTF-8 encoding
        switch (name){
            case "Chúng ta đã mỉm cười ":
                listItem.add(new Paragraph("Chúng ta đã mỉm cười "));
                break;
            case "Khởi hành ":
                listItem.add(new Paragraph("Khởi hành "));
                break;

            case "Mọi thứ đều có thể thay đổi ":
                listItem.add(new Paragraph("Mọi thứ đều có thể thay đổi "));
                break;
            case "Làm chủ các mẫu thiết kế kinh điển trong lập trình":
                listItem.add(new Paragraph("Làm chủ các mẫu thiết kế kinh điển trong lập trình"));
                break;

            default:
                Log.e("Err", "onMessageReceived: ",null );

        }

        listItem.add(new Paragraph(numberBorBook));
        listItem.add(new Paragraph(new String(numberReturnBook.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)));
        listItem.add(new Paragraph(new String(revenue.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)));
        listItem.add(new Paragraph(new String(inventory.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)));



        return listItem;
    }



}