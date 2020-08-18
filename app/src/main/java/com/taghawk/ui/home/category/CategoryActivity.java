package com.taghawk.ui.home.category;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;

/**
 * Created by Amar kumar on 23-01-2019.
 */

public class CategoryActivity extends BaseActivity {

    @Override
    protected int getResourceId() {
        return R.layout.activity_category_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFlag();
        setHeader(getString(R.string.categories));
        setLisener();
        addCategoryFragment();
    }

    private void setLisener() {
        ((AppCompatImageView) findViewById(R.id.iv_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((AppCompatTextView) findViewById(R.id.tv_reset)).setVisibility(View.GONE);
        ((AppCompatTextView) findViewById(R.id.tv_reset)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("CATEGORY_ID", "");
                intent.putExtra("TITLE", "");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setFlag() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void addCategoryFragment() {
        addFragment(R.id.container, new CategoryFragment(), CategoryFragment.class.getSimpleName());
    }


}
