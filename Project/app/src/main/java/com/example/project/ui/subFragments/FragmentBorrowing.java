package com.example.project.ui.subFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.project.DataManager;
import com.example.project.R;
import com.example.project.entities.Receipt;
import com.example.project.ui.custom_adapter.CustomReceiptAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentBorrowing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentBorrowing extends Fragment {
    private ListView listView;
    private CustomReceiptAdapter adapter;
    private View view;
    public FragmentBorrowing() {
        // Required empty public constructor
    }

    public static FragmentBorrowing newInstance(String param1, String param2) {
        FragmentBorrowing fragment = new FragmentBorrowing();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sub_fragment_borrowing, container, false);
        View rootView = inflater.inflate(R.layout.fragment_note, container, false);

        updateListView();
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Lấy reference của tabLayout từ activity chứa fragment
        updateListView();
        TabLayout tabLayout = getActivity().findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Khi người dùng chọn một tab, cập nhật lại giao diện của fragment
                updateListView();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateListView();

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                updateListView();
            }
        });

    }
    public void updateListView() {
        if (adapter == null){
            adapter = new CustomReceiptAdapter(getContext(), R.layout.list_item_receipt);}

        listView = view.findViewById(R.id.listViewBorrowing);
        Receipt[]  allReceipts = DataManager.getInstance().getReceipts();

        if ( allReceipts != null) {

            List<Receipt> returnReceipts = new ArrayList<>();
            for (Receipt receipt : allReceipts) {
                if ("Borrowing".equals(receipt.getStatus())) {
                    returnReceipts.add(receipt);
                }

            }
            adapter.clear();
            adapter.addAll(returnReceipts);
            listView.setAdapter(adapter);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        updateListView();
    }
}