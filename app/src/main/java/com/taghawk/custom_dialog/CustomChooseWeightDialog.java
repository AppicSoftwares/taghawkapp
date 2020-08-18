package com.taghawk.custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.taghawk.R;
import com.taghawk.adapters.ChooseWeightAdapter;
import com.taghawk.databinding.DialogChooseWeightBinding;
import com.taghawk.interfaces.OnDialogItemObjectClickListener;
import com.taghawk.model.AddProduct.ChooseWeightModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by appinventiv on 27/3/18.
 */

public class CustomChooseWeightDialog extends Dialog implements View.OnClickListener {

    private OnDialogItemObjectClickListener dialogCallback;
    private DialogChooseWeightBinding mDialogBinding;
    private Context mActivity;

    public CustomChooseWeightDialog(@NonNull Context mActivity, OnDialogItemObjectClickListener callback) {
        super(mActivity);
        dialogCallback = callback;
        this.mActivity = mActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogBinding = DialogChooseWeightBinding.inflate(LayoutInflater.from(getContext()));
        this.setContentView(mDialogBinding.getRoot());
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void setUpView(ArrayList<ChooseWeightModel> weightList) {
        mDialogBinding.rvWeights.setLayoutManager(new LinearLayoutManager(mActivity));
        ChooseWeightAdapter adapter = new ChooseWeightAdapter(weightList, this);
        mDialogBinding.rvWeights.setAdapter(adapter);
    }

    private void initView() {
        mDialogBinding.ivCancel.setOnClickListener(this);
        ArrayList<ChooseWeightModel> weightList = loadJSONFromAsset();
        if (weightList.size() > 0) {
            setUpView(weightList);
        }
    }

    private ArrayList<ChooseWeightModel> loadJSONFromAsset() {
        ArrayList<ChooseWeightModel> weightList = new ArrayList<>();
        String json = null;
        try {
            InputStream is = mActivity.getAssets().open("ShippingPrice.json");
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
                JSONArray m_jArry = jsonObject.getJSONArray("shippingPrirceDetails");
                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);
                    ChooseWeightModel weightListBean = new ChooseWeightModel();
                    String weight = jo_inside.getString("weight");
                    Double fedexPrice = jo_inside.getDouble("fedexPrice");
                    weightListBean.setWeight(weight);
                    weightListBean.setFedexPrice(fedexPrice);
                    weightList.add(weightListBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return weightList;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                break;
            case R.id.tv_choose_weight:
                ChooseWeightModel model = (ChooseWeightModel) v.getTag();
                dialogCallback.onPositiveBtnClick(model);
                break;


        }
        dismiss();

    }
}
