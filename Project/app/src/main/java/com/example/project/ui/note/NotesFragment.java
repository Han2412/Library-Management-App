package com.example.project.ui.note;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.project.DataManager;
import com.example.project.R;
import com.example.project.databinding.FragmentNoteBinding;
import com.example.project.entities.Book;
import com.example.project.entities.Receipt;
import com.example.project.ui.CaptureAct;
import com.example.project.ui.DetailReceiptActivity;
import com.example.project.ui.subFragments.SubFragmentAdapter;
import com.example.project.utils.Constants;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {

    private FragmentNoteBinding binding;
    private static String scannedContent = "";
    private static String previousScannedContent = "";
    private SubFragmentAdapter subFragmentAdapter;
    EditText searchEditText;
    ImageButton btn_remove;
    Button searchButton;
    TabLayout tabLayout;
    ViewPager viewPager;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNoteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize TabLayout and ViewPager
        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;

        viewPager.setAdapter(new SubFragmentAdapter(getChildFragmentManager(), Constants.MODE_SUB_TABS.NOTE));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(
                getResources().getColorStateList(R.color.disable).getDefaultColor(), // Unselected color
                getResources().getColorStateList(R.color.main).getDefaultColor()
        );

        ImageButton btnScan = binding.btnScanReturn;
        btnScan.setOnClickListener(v -> {
            scanCode();

        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection
            }
        });
        searchEditText = binding.searchEditTextNote;

        searchButton = binding.searchButtonNote;
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DataManager.getInstance().ReceiptsFillter = null;
            }

            @Override
            public void afterTextChanged(Editable s) {
                DataManager.getInstance().ReceiptsFillter = null;
            }

        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchEditText.getText().toString().equals("")) {
                    Receipt[] receipts = DataManager.getInstance().getReceipts();
                    DataManager.getInstance().ReceiptsFillter = filterBooks(searchEditText.getText().toString(), receipts);
                }

                viewPager.setAdapter(null);

                tabLayout = binding.tabLayout;
                viewPager = binding.viewPager;

                subFragmentAdapter = new SubFragmentAdapter(getChildFragmentManager(), Constants.MODE_SUB_TABS.NOTE);
                viewPager.setAdapter(subFragmentAdapter);
            }
        });
        return root;
    }

    public Receipt[] filterBooks(String searchText, Receipt[] receipts) {
        List<Receipt> result = new ArrayList<>();
        for (Receipt receipt : receipts) {
            if (receipt.first_name.contains(searchText)||receipt.last_name.contains(searchText) ||receipt.id.contains(searchText)) {
                result.add(receipt);
            }
        }
        return result.toArray(result.toArray(new Receipt[0]));
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

        if (result.getContents() != null) {
             scannedContent = result.getContents();
            String previousScannedContent = "";

            if (!scannedContent.equals(previousScannedContent)) {
                DataManager.getInstance().ReceiptsFillter = null;
                previousScannedContent = scannedContent;

                if (!scannedContent.toString().equals("")) {
                    Receipt[] receipts = DataManager.getInstance().getReceipts();
                    DataManager.getInstance().ReceiptsFillter = filterBooks(scannedContent.toString(), receipts);
                    viewPager.setAdapter(null); // Clear the current adapter
                    tabLayout = binding.tabLayout;
                    viewPager = binding.viewPager;

                    subFragmentAdapter = new SubFragmentAdapter(getChildFragmentManager(), Constants.MODE_SUB_TABS.NOTE);
                    viewPager.setAdapter(subFragmentAdapter);
                }
            }

        }
    });
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().ReceiptsFillter = null;
<<<<<<< HEAD
        System.out.println("Note " + scannedContent.toString());
=======
>>>>>>> a0c7bce074c1be559e595582893b61d5fcfd61d6
        Receipt[] receipts = DataManager.getInstance().getReceipts();
        DataManager.getInstance().ReceiptsFillter = filterBooks(scannedContent.toString(), receipts);
        viewPager.setAdapter(null); // Clear the current adapter
        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;

        subFragmentAdapter = new SubFragmentAdapter(getChildFragmentManager(), Constants.MODE_SUB_TABS.NOTE);
        viewPager.setAdapter(subFragmentAdapter);


  }
}