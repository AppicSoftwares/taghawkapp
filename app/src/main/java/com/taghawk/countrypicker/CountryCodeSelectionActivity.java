package com.taghawk.countrypicker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taghawk.R;
import com.taghawk.adapters.CountrySelectionAdapter;
import com.taghawk.base.BaseActivity;
import com.taghawk.model.SelectionListBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class CountryCodeSelectionActivity extends BaseActivity {

    @BindView(R.id.et_search_bar)
    EditText edtSearch;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.iv_cancel_search)
    AppCompatImageView ivCancelSearch;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    private CountrySelectionAdapter selectionAdapter;
    @BindView(R.id.iv_back)
    AppCompatImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_code_selection);
        ButterKnife.bind(this);
        settingListeners();
        ArrayList<SelectionListBean> countryCodeList = loadJSONFromAsset();
        if (countryCodeList.size() > 0) {
            setAdapter(countryCodeList);
        }
    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_country_code_selection;
    }


    /*Setting up listeners*/
    private void settingListeners() {
        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                selectionAdapter.getFilter().filter(cs.toString().trim());
                if (edtSearch.getText().toString().trim().length() > 0) {
                    ivCancelSearch.setVisibility(View.VISIBLE);
                } else {
                    ivCancelSearch.setVisibility(View.GONE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }


    public ArrayList<SelectionListBean> loadJSONFromAsset() {
        ArrayList<SelectionListBean> countryList = new ArrayList<>();
        String json = null;
        try {
            InputStream is = getAssets().open("CountryCode.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray m_jArry = jsonObject.getJSONArray("countryDetails");
                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);
                    SelectionListBean selectionListBean = new SelectionListBean();
                    String country = jo_inside.getString("name");
                    String code = jo_inside.getString("dial_code");
                    selectionListBean.setName(country);
                    selectionListBean.setId(code);
                    countryList.add(selectionListBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return countryList;
    }

    private void setAdapter(List<SelectionListBean> dataList) {
        selectionAdapter = new CountrySelectionAdapter(dataList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(mLayoutManager);
        rvList.setAdapter(selectionAdapter);
    }

    /**
     * Sending data back to previous activity
     *
     * @param selectionListBean
     */
    public void sendDataBack(SelectionListBean selectionListBean) {
        Intent intent = new Intent();
        intent.putExtra("Name", "" + selectionListBean.getName());
        intent.putExtra("Id", "" + selectionListBean.getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick({R.id.iv_back, R.id.iv_cancel_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_cancel_search:
                edtSearch.setText("");
                break;
        }
    }

    private void finishActivity() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

}
