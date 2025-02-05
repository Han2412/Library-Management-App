package com.example.project.ui.subFragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.project.DataManager;
import com.example.project.R;
import com.example.project.entities.Receipt;
import com.example.project.ui.custom_adapter.CustomCancelAdapter;
import com.example.project.ui.custom_adapter.CustomReturnAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentReturned#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentReturned extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    CustomReturnAdapter adapter;
    ListView listView;
    View view;
    private FragmentReturned binding;
    private SubFragmentAdapter subFragmentAdapter;
    private Button btnCancel;
    public FragmentReturned() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentBorrowing.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentReturned newInstance(String param1, String param2) {
        FragmentReturned fragment = new FragmentReturned();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Receipt receipts; // Đối tượng Receipts để lưu trữ dữ liệu

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentReturned fragmentReturned = new FragmentReturned();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.sub_fragment_return, container, false);
        listView = view.findViewById(R.id.listreturn);
        updateListView();
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Lấy reference của tabLayout từ activity chứa fragment
        TabLayout tabLayout =getActivity().findViewById(R.id.tabLayout);

        // Thiết lập sự kiện cho tabLayout
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
            }
        });

        // Khi fragment được tạo ra lần đầu tiên, cập nhật giao diện
        updateListView();
    }
    public void updateListView() {
        if(adapter==null){
        adapter = new CustomReturnAdapter(getContext(), R.layout.list_item_return);}
        listView = view.findViewById(R.id.listreturn);
        Receipt[] allReceipts = DataManager.getInstance().getReceipts();
//        System.out.println("110   "+allReceipts.length);
        if (allReceipts != null) { // Kiểm tra xem allReceipts có null không
            List<Receipt> returnReceipts = new ArrayList<>();

            // Lọc và chỉ chọn những Receipt có status là "Return"
            for (Receipt receipt : allReceipts) {
                if ("Return".equals(receipt.getStatus())) {
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