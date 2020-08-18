package com.taghawk.ui.home.filter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.taghawk.R;
import com.taghawk.adapters.PlaceAutoCompleteAdapter;
import com.taghawk.databinding.ActivityChoosseLocationBinding;

import butterknife.ButterKnife;


public class ChoosseLocationActivity extends AppCompatActivity {

    private ActivityChoosseLocationBinding mBining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBining = DataBindingUtil.setContentView(this, R.layout.activity_choosse_location);
        ButterKnife.bind(this);
        settingAdapter();
        initView();

    }

    private void initView() {
        mBining.suggestionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Utils.hideKeyBoard(ChoosseLocationActivity.this);
                String location = (String) adapterView.getItemAtPosition(i);
                Intent in = new Intent();
                in.putExtra("Location", "" + location);
                setResult(10, in);
                finish();
            }
        });
    }

    /*Google places autocomplete adapter list view*/
    private void settingAdapter() {
        final PlaceAutoCompleteAdapter adapter = new PlaceAutoCompleteAdapter(this, R.layout.choose_location_list_item);
        mBining.suggestionListView.setAdapter(adapter);

        mBining.edtEnterSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }
        });
        mBining.toolbar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

