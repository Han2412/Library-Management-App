package com.example.project.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.example.project.network.OnMessageReceivedListenner;
import com.example.project.network.WebSockerClientBarChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookStatistics extends AppCompatActivity {
    private EditText edtDayA,edtDayB;
    private Button btnStatistics,btnbtnStatisticsDetail;

    private WebSockerClientBarChart webSockerClientBarChart;

    BarChart barChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_statistics);
        setControl();

        webSockerClientBarChart = new WebSockerClientBarChart();
        webSockerClientBarChart.connectWebSockert();
        try {
            Thread.sleep(1000); // Chờ 1 giây (1000 milliseconds)
        } catch (InterruptedException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }
        setEvent();
        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dayA = edtDayA.getText().toString();
                String dayB = edtDayB.getText().toString();
                String event = "Statistical";
                webSockerClientBarChart.putDataEventBarChart(event,dayA,dayB);

            }
        });

        btnbtnStatisticsDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(BookStatistics.this, StatisticsDetail.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        webSockerClientBarChart.setOnMessageReceivedListener(new OnMessageReceivedListenner() {
            @Override
            public void onMessageReceived(String message) {
                try {
                    JSONObject response = new JSONObject(message);
                    JSONArray idbookArray = response.getJSONArray("idbook");

                    List<Integer> dataY = new ArrayList<>();
                    List<String> lables = new ArrayList<>();
                    for (int i = 0; i < idbookArray.length(); i++) {
                        JSONObject bookObject = idbookArray.getJSONObject(i);
                        String tensach = bookObject.getString("tensach");
                        int luotsachmuon = bookObject.getInt("tongluotmuon");
                        dataY.add(luotsachmuon);
                        lables.add(tensach);
                        Log.i("NHANNNNNNN", "Tên sách: " + tensach + ", Số lượt mượn: " + luotsachmuon);
                    }
                    setupChart(dataY,lables);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setEvent() {

        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar currentDate = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        String formattedFirstDay = sdf.format(firstDayOfMonth.getTime());


        // Định dạng và hiển thị ngày hiện tại của tháng này
        String formattedCurrentDate = sdf.format(currentDate.getTime());

        Log.i("test", "setEvent: " + formattedFirstDay+" kkk "+formattedCurrentDate);

        String event = "Statistical";
        webSockerClientBarChart.putDataEventBarChart(event,formattedFirstDay,formattedCurrentDate);
    }

    private void setControl() {
        edtDayA = findViewById(R.id.editTextDateA);
        edtDayB = findViewById(R.id.editTextDateB);
        btnStatistics =findViewById(R.id.buttonStatistic);
        barChart = findViewById(R.id.BarChart);
        btnbtnStatisticsDetail=findViewById(R.id.buttonStatisticDetail);
    }
    public void showDatePickerDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final EditText selectedEditText = (EditText) view;
        DatePickerDialog datePickerDialog = new DatePickerDialog(BookStatistics.this,
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


    private void setupChart(List<Integer> dataY,List<String> lables) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for(int i = 0;i<dataY.size();i++){
            entries.add(new BarEntry(0.5F+i, dataY.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Thống kê sách mượn");
        dataSet.setValueTextSize(10f); // Đặt kích thước của giá trị trên cột

        BarData barData = new BarData(dataSet);

        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(lables));
        barChart.getXAxis().setGranularity(1f); // Đảm bảo rằng các nhãn được hiển thị mỗi 1 cột
        barChart.getXAxis().setCenterAxisLabels(true); // Căn chỉnh nhãn ở giữa cột
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Đặt trục x ở dưới biểu đồ
        barChart.getXAxis().setTextSize(10f); // Đặt kích thước của nhãn trục X

        // Đặt giá trị tối thiểu cho trục Y là 0
        barChart.getAxisLeft().setAxisMinimum(0f);

        // Ẩn trục Y bên phải
        barChart.getAxisRight().setEnabled(false);
        // Tùy chỉnh thêm nếu cần
        barChart.getDescription().setEnabled(false); // Mô tả biểu đồ
        barChart.setFitBars(true); // Tự động điều chỉnh chiều rộng cột để phù hợp với không gian hiển thị
        barChart.invalidate(); // Cập nhật biểu đồ
    }

}