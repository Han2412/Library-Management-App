package com.example.project.ui.custom_adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project.R;
import com.example.project.entities.Book;
import com.example.project.entities.Receipt;
import com.example.project.ui.DetailReceiptActivity;
import com.example.project.ui.subFragments.OnSelectButtonClickListener;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

// CustomBookAdapter.java
public class CustomDetailOrderAdapter extends ArrayAdapter<Book> {
    public CustomDetailOrderAdapter(Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_book_review, parent, false);
        }

        // Get the current Book object
        Book book = getItem(position);

        ImageView image = convertView.findViewById(R.id.image_book);
        byte[] decodedString = Base64.decode(book.image, Base64.DEFAULT);
        if (decodedString != null && decodedString.length > 0){
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            book.decodedByte = decodedByte;
            image.setImageBitmap(decodedByte);
        }

        // Set the data to the views in the list item layout
        TextView txtName = convertView.findViewById(R.id.name_book);
        TextView txtAuthorName = convertView.findViewById(R.id.author_name);
        TextView txtSummary = convertView.findViewById(R.id.summary);

        txtName.setText(book.name);
        txtAuthorName.setText(book.name_author);
        txtSummary.setText(book.summary);

        return convertView;
    }
}