package com.example.project.ui.subFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.project.DataManager;
import com.example.project.R;
import com.example.project.entities.Book;
import com.example.project.ui.custom_adapter.CustomBookAdapter;

import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAll#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAll extends Fragment {
    private ListView listView;
    private CustomBookAdapter adapter;

    View view;

    private TextView textNone;

    public FragmentAll() {
        // Required empty public constructor
    }

    public static FragmentAll newInstance(String param1, String param2) {
        FragmentAll fragment = new FragmentAll();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sub_fragment_all, container, false);

        listView = view.findViewById(R.id.listView);
        textNone = view.findViewById(R.id.text_none);

        refresh();

        return view;
    }

    public void refresh(){
        Button myButton = getParentFragment().getView().findViewById(R.id.myButton);
        myButton.setText("Check Out +" + DataManager.getInstance().getBooksSelect().size());

        listView = view.findViewById(R.id.listView);
        if(adapter == null) adapter = new CustomBookAdapter(requireContext(), R.layout.list_item_book, true);
        adapter.setOnSelectButtonClickListener(new OnSelectButtonClickListener(){
            @Override
            public void onSelectButtonClick() {
                myButton.setText("Check Out +" + DataManager.getInstance().getBooksSelect().size());
            }
        });
        System.out.println("Borowing   ");
        // Assuming DataManager.getInstance().getBooks() returns an array of Book objects
        Book[] books = DataManager.getInstance().getBooks();
        adapter.clear();
        adapter.addAll(books); // Pass the array of Book objects to the adapter

        listView.setAdapter(null);
        listView.setAdapter(adapter);
    }
//    public void refresh(String txtSearch){
//        Book[] books = null;
//        if(txtSearch == "") books = DataManager.getInstance().getBooks();
//        else{
//            Book[] booksData = DataManager.getInstance().getBooks();
//            books = Arrays.stream(booksData)
//                    .filter(book -> book.name.contains(txtSearch) || book.name_author.contains(txtSearch))
//                    .toArray(Book[]::new);
//        }
//        if(books != null){
//            textNone.setVisibility(View.GONE);
//
//            Button myButton = getParentFragment().getView().findViewById(R.id.myButton);
//
//            listView = view.findViewById(R.id.listView);
//            if(adapter == null) adapter = new CustomBookAdapter(requireContext(), R.layout.list_item_book, true);
//            adapter.setOnSelectButtonClickListener(new OnSelectButtonClickListener(){
//
//                @Override
//                public void onSelectButtonClick() {
//                    myButton.setText("Check Out +" + DataManager.getInstance().getBooksSelect().size());
//                }
//            });
//
//            adapter.clear();
//            adapter.addAll(books); // Pass the array of Book objects to the adapter
//
//            listView.setAdapter(adapter);
//        }
//        else{
//            textNone.setVisibility(View.VISIBLE);
//        }
//    }
    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}