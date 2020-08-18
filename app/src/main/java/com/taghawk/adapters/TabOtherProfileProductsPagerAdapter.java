package com.taghawk.adapters;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.ui.profile.SavedProductsFragment;
import com.taghawk.ui.profile.SellingProductsFragment;
import com.taghawk.ui.profile.SoldProductsFragment;

public class TabOtherProfileProductsPagerAdapter extends FragmentPagerAdapter {

    public static final Integer[] tabNames = {R.string.tab_selling, R.string.tab_sold};//Tab title array

    AppCompatActivity mActivity;
    private String sellerId;

    public TabOtherProfileProductsPagerAdapter(FragmentManager manager, AppCompatActivity ac, String sellerId) {
        super(manager);
        mActivity = ac;
        this.sellerId = sellerId;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.BUNDLE_DATA, sellerId);
        switch (position) {
            case 0:
                SellingProductsFragment tab1;
                tab1 = new SellingProductsFragment();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                SoldProductsFragment tab2;
                tab2 = new SoldProductsFragment();
                tab2.setArguments(bundle);
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNames.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        return mActivity.getResources().getString(tabNames[position]);
    }

}