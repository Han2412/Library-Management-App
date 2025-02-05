package com.example.project.ui.home;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.project.DataManager;
import com.example.project.R;
import com.example.project.databinding.FragmentHomeBinding;
import com.example.project.entities.Book;
import com.example.project.ui.CaptureAct;
import com.example.project.ui.DetailOrderActivity;
import com.example.project.ui.IntroActivity;
import com.example.project.ui.LoginActivity;
import com.example.project.ui.subFragments.FragmentAll;
import com.example.project.ui.subFragments.SubFragmentAdapter;
import com.example.project.utils.Constants;
import com.google.android.material.tabs.TabLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private SubFragmentAdapter subFragmentAdapter;
    EditText searchEditText;
    ImageButton btn_remove;
    Button searchButton;
    TabLayout tabLayout;
    ViewPager viewPager;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ImageButton btnScan = binding.btnScan;
        btnScan.setOnClickListener(v -> {
            scanCode();
        });

        // Initialize TabLayout and ViewPager
        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;

        subFragmentAdapter = new SubFragmentAdapter(getChildFragmentManager(), Constants.MODE_SUB_TABS.HOME);

        viewPager.setAdapter(subFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(
                getResources().getColorStateList(R.color.disable).getDefaultColor(), // Unselected color
                getResources().getColorStateList(R.color.main).getDefaultColor()
        );

        binding.myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), DetailOrderActivity.class));
            }
        });

        // Optional: Set a listener to handle tab selection events
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselection
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection
            }
        });

        searchEditText = binding.searchEditText;
        btn_remove = binding.btnRemove;
        searchButton = binding.searchButton;
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
                btn_remove.setVisibility(View.GONE);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DataManager.getInstance().booksFilter = null;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btn_remove.setVisibility(View.VISIBLE);

                } else {
                    btn_remove.setVisibility(View.GONE);
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchEditText.getText().toString().equals("")) {
                    Book[] books = DataManager.getInstance().getBooks();
                    DataManager.getInstance().booksFilter = filterBooks(searchEditText.getText().toString(), books);
                }

                viewPager.setAdapter(null);

                tabLayout = binding.tabLayout;
                viewPager = binding.viewPager;

                subFragmentAdapter = new SubFragmentAdapter(getChildFragmentManager(), Constants.MODE_SUB_TABS.HOME);
                viewPager.setAdapter(subFragmentAdapter);
            }
        });

        return root;
    }

    public Book[] filterBooks(String searchText, Book[] books) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.name.contains(searchText) || book.name_author.contains(searchText)) {
                result.add(book);
            }
        }
        return result.toArray(result.toArray(new Book[0]));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null){
            Book book = DataManager.getInstance().getBookByID(result.getContents());
            System.out.println("scan " +result.getContents());
            DataManager.getInstance().addBookSelect(book);
            binding.myButton.setText("Check Out +" + DataManager.getInstance().getBooksSelect().size());
        }
    });
}
