package com.example.project.ui.custom_adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project.DataStatistics;
import com.example.project.R;

import java.util.ArrayList;

public class StatisticsAdapter extends ArrayAdapter {

    Activity context;
    int idLayout;
    ArrayList<DataStatistics> dataStatistics;

    public StatisticsAdapter(Activity context, int idLayout, ArrayList<DataStatistics> dataStatistics) {
        super(context, idLayout,dataStatistics);
        this.context = context;
        this.idLayout = idLayout;
        this.dataStatistics = dataStatistics;
    }

    //Gọi hàm getView để tiến hành sắp xếp dữ liệu

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        //đặt id vào để tạo thành view
        convertView = inflater.inflate(idLayout,null);

        DataStatistics data = dataStatistics.get(position);

        ImageView imageView = convertView.findViewById(R.id.imgViewStatistic);
        TextView tvNameBook = convertView.findViewById(R.id.nameBook);
        TextView tvNumberBorBook = convertView.findViewById(R.id.numberBorBook);
        TextView tvNumberReturnBook = convertView.findViewById(R.id.numberReturnBook);
        TextView tvRevenue = convertView.findViewById(R.id.revenue);
        TextView tvInventory = convertView.findViewById(R.id.inventory);

        imageView.setImageResource(data.getImage());
        tvNameBook.setText(data.getNameBook());
        tvNumberBorBook.setText(data.getNumberBorBook());
        tvNumberReturnBook.setText(data.getNumberReturnBook());
        tvRevenue.setText(data.getRevenue());
        tvInventory.setText(data.getInventory());

        return convertView;

    }
}
