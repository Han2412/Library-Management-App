package com.example.project.ui.statistics;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project.databinding.FragmentDashboardBinding;
import com.example.project.ui.BookStatistics;

public class StatisticsFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Khởi tạo Intent để mở BookStatistics Activity
        Intent intent = new Intent(getActivity(), BookStatistics.class);
        startActivity(intent);

        // Trả về null vì không có giao diện để hiển thị
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}