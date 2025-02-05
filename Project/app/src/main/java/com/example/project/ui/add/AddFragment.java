package com.example.project.ui.add;

import static android.provider.MediaStore.Images.Media.getBitmap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project.DataManager;
import com.example.project.R;
import com.example.project.databinding.FragmentAddBinding;
import com.example.project.entities.Book;
import com.example.project.entities.Category;
import com.example.project.entities.CategoryResponse;
import com.example.project.entities.DataResponse;
import com.example.project.network.SocketEventListener;
import com.example.project.network.WebSocketClient;
import com.example.project.ui.MainActivity;
import com.example.project.utils.Constants;
import com.example.project.utils.LoadingDialog;
import com.example.project.utils.PopupUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddFragment extends Fragment implements SocketEventListener {

    private FragmentAddBinding binding;
    TextView textView;
    List<String> data_lsp = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String selectedCategory, newCategory;
    byte[] byteArray;
    String base64Image;
    Bitmap bitmap;
    AlertDialog dialog;
    Button btnAdd, btnAddCategory, btnQuitAddCategory;
    ImageButton ibSelectImage, ibCalendar, ibAddBookCategory;
    ImageView imageView;
    TextView textView1;
    EditText edtBookName, edtAuthorName, edtQuantity, editDateToAdd, edtSummary, edtBookId, edtBookPrice;
    Spinner spnBookCategory;

    ActivityResultLauncher<Intent> resultLauncher;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddViewModel addViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);

        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SetControl();
        registerResult();
        SetEvent();

        addViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void SetEvent() {
        // Create spinner
        CreateSpinner();
//        adapter = new ArrayAdapter<String>(getContext(), R.layout.category_iem_spinner,data_lsp);
//        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
//        spnBookCategory.setAdapter(adapter);

        spnBookCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtBookId.getText().toString())){
                    Toast.makeText(getContext(), "Please enter book id", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(edtBookName.getText().toString())){
                    Toast.makeText(getContext(), "Please enter book name", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(edtAuthorName.getText().toString())){
                    Toast.makeText(getContext(), "Please enter author's name", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(edtSummary.getText().toString())){
                    Toast.makeText(getContext(), "Please enter summary", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(edtQuantity.getText().toString())){
                    Toast.makeText(getContext(), "Please enter quantity", Toast.LENGTH_LONG).show();
                }
                if (selectedCategory.equals("")){
                    Toast.makeText(getContext(), "Please select category", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(edtBookPrice.getText().toString())){
                    Toast.makeText(getContext(), "Please enter book price", Toast.LENGTH_LONG).show();
                }
                LoadingDialog.getInstance(getContext()).show();

                Gson gson = new Gson();
                Book book = new Book();

                book.setId(edtBookId.getText().toString());
                book.setName(edtBookName.getText().toString());
                book.setSummary(edtSummary.getText().toString());
                book.setName_author(edtAuthorName.getText().toString());
                book.setInventory_quantity(Integer.parseInt(edtQuantity.getText().toString()));
                book.setCategory(selectedCategory);
                // Convert image
                ByteArrayOutputStream byteArrayOutputStream;
                byteArrayOutputStream = new ByteArrayOutputStream();
                if (bitmap != null){
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    //Log.d("MyTag", base64Image);
                }
                book.setImage(base64Image);

                EditText edit_date_from = editDateToAdd;
                DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String formattedDate = edit_date_from.getText().toString();
                try {
                    Date date = inputFormat.parse(formattedDate);

                    formattedDate = outputFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                book.setDate_add(formattedDate);

                book.setPrice(Integer.parseInt(edtBookPrice.getText().toString()));

                JSONObject addBookObject = new JSONObject();

                try {
                    addBookObject.put("event", Constants.EVENT_ADD_BOOK);
                    addBookObject.put("book", gson.toJson(book));
                    addBookObject.put("username", DataManager.getInstance().username);
                    String mess = addBookObject.toString();

                    WebSocketClient.getInstance().requestToServer(mess, AddFragment.this);

                    edtBookName.setText("");
                    edtAuthorName.setText("");
                    edtSummary.setText("");
                    edtQuantity.setText("");
                    editDateToAdd.setText("");
                    imageView.setImageDrawable(null);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        ibAddBookCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_book_category, null);
                builder.setView(dialogView);

                final EditText edtNewCategory = dialogView.findViewById(R.id.edtNewCategory);

                Button addCategoryBtn = dialogView.findViewById(R.id.addCategoryBtn);
                addCategoryBtn.setBackgroundColor(Color.GREEN);

                Button quitAddCategoryBtn = dialogView.findViewById(R.id.quitAddCategoryBtn);
                quitAddCategoryBtn.setBackgroundColor(Color.RED);

                dialog = builder.create();

                addCategoryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newCategory = edtNewCategory.getText().toString();
                        if (!newCategory.isEmpty()) {

                            Gson gson = new Gson();
                            Category category = new Category();
                            category.setName(newCategory);
                            JSONObject addCategoryObject = new JSONObject();
                            try {
                                addCategoryObject.put("event", Constants.EVENT_ADD_CATEGORY);
                                addCategoryObject.put("category", gson.toJson(category));
                                addCategoryObject.put("username", DataManager.getInstance().username);
                                String mess = addCategoryObject.toString();

                                WebSocketClient.getInstance().requestToServer(mess, AddFragment.this);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Toast.makeText(getContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                quitAddCategoryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        ibSelectImage.setOnClickListener(view -> pickImage());
        ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar();
            }
        });
    }

    private void openCalendar(){
        Calendar calendar = Calendar.getInstance();
        int defaultYear = calendar.get(Calendar.YEAR);
        int defaultMonth = calendar.get(Calendar.MONTH); // Tháng bắt đầu từ 0
        int defaultDay = calendar.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editDateToAdd.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year));
            }
        },
                defaultYear, defaultMonth, defaultDay);

        dialog.show();
    }

    private void pickImage(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private  void registerResult(){
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();

                            bitmap = getBitmap(getActivity().getContentResolver(), imageUri);
                            //Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                            imageView.setImageBitmap(bitmap);
                        } catch (Exception e){
                            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private  void  SetControl() {
        textView = binding.textAdd;
        textView1 = binding.textView1;

        edtBookName = binding.edtBookName;
        edtAuthorName = binding.edtAuthorName;
        edtQuantity = binding.edtQuantity;
        editDateToAdd = binding.editDateToAdd;
        edtSummary = binding.edtSummary;
        edtBookId = binding.edtBookId;
        edtBookPrice = binding.edtPrice;

        btnAdd = binding.buttonAdd;

        ibSelectImage = binding.ibBookImage;
        ibAddBookCategory = binding.addBookCategory;
        ibCalendar = binding.ibDateToAdd;
        imageView = binding.ivBookImage;

        spnBookCategory = binding.spnBookCategory;
    }

    private void CreateSpinner(){
        // Get categories from db
        JSONObject categoryObject = new JSONObject();
        try {
            categoryObject.put("event", Constants.EVENT_GET_CATEGORIES);
            categoryObject.put("username", DataManager.getInstance().username);
            String mess = categoryObject.toString();
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
        LoadingDialog.getInstance(getContext()).hide();
        startActivity(new Intent(getContext(), MainActivity.class));
    }

    @Override
    public void onOrderResponse(boolean result) {

    }

    @Override
    public void onAddBookResponse(boolean result) throws JSONException {
        LoadingDialog.getInstance(getContext()).hide();
        if (result) {
            GetAllData();
            PopupUtils.showPopup(getContext(), "", "Add new book successfully.", Constants.TYPE_ALERT.OK, null, null);
        }
        else {
            PopupUtils.showPopup(getContext(), "Warning", "Add new book failed. Please try again.", Constants.TYPE_ALERT.OK, null, null);
        }
    }

    @Override
    public void onAddCategoryResponse(boolean result) throws JSONException {
        if (result){
            GetAllCategories();
            dialog.dismiss();
        }
        else {
            PopupUtils.showPopup(getContext(), "Warning", "Add new category failed. Please try again.", Constants.TYPE_ALERT.OK, null, null);
        }
    }

    @Override
    public void onGetCategoryResponse(String data) throws JSONException {
        Gson gson = new Gson();
        CategoryResponse categoryResponse = gson.fromJson(data, CategoryResponse.class);
        List<Category> categories = categoryResponse.getCategories();

        for (Category category: categories) {
            if (!data_lsp.contains(category.getName()))
                data_lsp.add(category.getName());
        }
        adapter = new ArrayAdapter<String>(getContext(), R.layout.category_iem_spinner,data_lsp);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        spnBookCategory.setAdapter(adapter);
    }

    @Override
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

    void GetAllCategories() throws JSONException {
        JSONObject categoryObject = new JSONObject();
        categoryObject.put("event", Constants.EVENT_GET_CATEGORIES);
        categoryObject.put("username", DataManager.getInstance().username);
        String mess = categoryObject.toString();
        WebSocketClient.getInstance().requestToServer(mess, this);
    }
}