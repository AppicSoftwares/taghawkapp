package com.taghawk.ui.home.tabs;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.taghawk.R;
import com.taghawk.adapters.ClusterAdapter;
import com.taghawk.databinding.LayoutClusterViewBinding;
import com.taghawk.model.tag.ClusterBean;

import java.util.ArrayList;

public class ClusterItemsActivity extends AppCompatActivity implements View.OnClickListener {
    private ClusterAdapter clusterAdapter;
    private ArrayList<ClusterBean> clusterBeanArrayList;
    private LayoutClusterViewBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_cluster_view);
        initView();
        getIntentData();
        initRecylerviewForCluster();

    }

    /*
     * initialize the list and views
     * */
    private void initView() {
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        StatusBarUtil.setTransparent(this);
        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        clusterBeanArrayList = new ArrayList<>();
//        mBinding.ivCross.setOnClickListener(this);
    }

    /*
     * get the data fro the intent @from home activity
     * */
    private void getIntentData() {
        clusterBeanArrayList = (ArrayList<ClusterBean>) getIntent().getSerializableExtra("LIST");
    }

    /*
     * initialize the recyler view for the cluster for thedialog
     * */
    private void initRecylerviewForCluster() {
        mBinding.rvCluster.setLayoutManager(new LinearLayoutManager(this));
        clusterAdapter = new ClusterAdapter(this, clusterBeanArrayList);
        mBinding.rvCluster.setAdapter(clusterAdapter);
    }


    private void closeActivity() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @Override
    public void onClick(View v) {
        closeActivity();
    }
}
