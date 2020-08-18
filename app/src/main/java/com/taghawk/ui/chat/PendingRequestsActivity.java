package com.taghawk.ui.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class PendingRequestsActivity extends BaseActivity {

    @Override
    protected int getResourceId() {
        return R.layout.activity_messages_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPendingRequestFragment();
    }

    /**
     * load the required fragment in the container
     */
    private void loadPendingRequestFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.TAG_KEY_CONSTENT.NAME, getIntent().getStringExtra(AppConstants.TAG_KEY_CONSTENT.NAME));
        bundle.putString(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, getIntent().getStringExtra(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID));
        bundle.putString(AppConstants.TAG_KEY_CONSTENT.IMAGE_URL, getIntent().getStringExtra(AppConstants.TAG_KEY_CONSTENT.IMAGE_URL));
        PendingRequestsFragment pendingRequestsFragment = new PendingRequestsFragment();
        pendingRequestsFragment.setArguments(bundle);
        addFragment(R.id.rl_base_container, pendingRequestsFragment, PendingRequestsFragment.class.getSimpleName());
    }
}
