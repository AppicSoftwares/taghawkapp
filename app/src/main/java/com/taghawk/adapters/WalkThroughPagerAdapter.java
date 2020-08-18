package com.taghawk.adapters;


import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.taghawk.constants.AppConstants;
import com.taghawk.ui.walkthrough.WalkThroughFirstFragment;
import com.taghawk.ui.walkthrough.WalkThroughFragment;


/**
 * Extending FragmentStatePagerAdapter
 */

public class WalkThroughPagerAdapter extends FragmentStatePagerAdapter {

    int tabCount;

    public WalkThroughPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:

                WalkThroughFragment tab1 = new WalkThroughFragment();
//                tab1.setArguments(bundleData(bundle, 1));
                return tab1;
            case 1:
                WalkThroughFirstFragment tab2 = new WalkThroughFirstFragment();
                tab2.setArguments(bundleData(bundle, 2));
                return tab2;
            case 2:
                WalkThroughFirstFragment tab3 = new WalkThroughFirstFragment();
                tab3.setArguments(bundleData(bundle, 3));
                return tab3;
            case 3:
                WalkThroughFirstFragment tab4 = new WalkThroughFirstFragment();
                tab4.setArguments(bundleData(bundle, 4));
                return tab4;
            default:
                return null;
        }
    }

    private Bundle bundleData(Bundle bundle, int action) {
        bundle.putInt(AppConstants.BUNDLE_DATA, action);
        return bundle;
    }

    @Override
    public int getItemPosition(Object object) {
        return tabCount;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}