package com.example.project.ui.subFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.project.entities.Book;
import com.example.project.ui.subFragments.FragmentAll;
import com.example.project.ui.subFragments.FragmentBorrowing;
import com.example.project.utils.Constants;

import java.util.Dictionary;
import java.util.Map;

public class SubFragmentAdapter extends FragmentPagerAdapter {

    private Constants.MODE_SUB_TABS modeSubTabs;

    public SubFragmentAdapter(FragmentManager fm, Constants.MODE_SUB_TABS modeSubTabs) {
        super(fm);
        this.modeSubTabs = modeSubTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if(modeSubTabs == Constants.MODE_SUB_TABS.HOME)
                    return new FragmentAll();
                if(modeSubTabs == Constants.MODE_SUB_TABS.NOTE)
                    return new FragmentBorrowing();
                break;
            case 1:
                if(modeSubTabs == Constants.MODE_SUB_TABS.NOTE)
                    return new FragmentReturned();
                break;
            case 2:
                if(modeSubTabs == Constants.MODE_SUB_TABS.NOTE)
                    return new FragmentCanceled();
                break;
            default:

        }return null;
    }

    @Override
    public int getCount() {
        if(modeSubTabs == Constants.MODE_SUB_TABS.HOME)
            return 1;
        if(modeSubTabs == Constants.MODE_SUB_TABS.NOTE)
            return 3;
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                if(modeSubTabs == Constants.MODE_SUB_TABS.HOME)
                    return "All";
                if(modeSubTabs == Constants.MODE_SUB_TABS.NOTE)
                    return "Borrow";
            case 1:
                if(modeSubTabs == Constants.MODE_SUB_TABS.NOTE)
                    return "Returned";
            case 2:
                if(modeSubTabs == Constants.MODE_SUB_TABS.NOTE)
                    return "Canceled";
            default:
                return "";
        }
    }
}
