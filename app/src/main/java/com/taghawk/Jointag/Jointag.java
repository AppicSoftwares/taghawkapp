package com.taghawk.Jointag;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.ui.home.tabs.TagListingFragment2;

public class Jointag extends BaseActivity {

    private FrameLayout idframe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jointag);
        initView();
        FragmentManager fm1 = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm1.beginTransaction();
        fragmentTransaction.replace(R.id.idframe, new TagListingFragment2());
        fragmentTransaction.commit();
    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_search2;
    }

    private void initView() {
        idframe = (FrameLayout) findViewById(R.id.idframe);
    }
}
